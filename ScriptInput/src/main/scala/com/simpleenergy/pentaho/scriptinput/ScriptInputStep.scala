package com.simpleenergy.pentaho.scriptinput

import org.pentaho.di.core.Counter
import org.pentaho.di.core.database.DatabaseMeta
import org.pentaho.di.core.exception.KettleStepException
import org.pentaho.di.core.row._
import org.pentaho.di.core.variables.VariableSpace
import org.pentaho.di.trans._
import org.pentaho.di.trans.step._

class ScriptInputStep(smi: StepMeta, sdi: StepDataInterface, copyNr: Int, transMeta: TransMeta, trans: Trans) extends BaseStep(smi, sdi, copyNr, transMeta, trans) {
  override def processRow(smi: StepMetaInterface, sdi: StepDataInterface): Boolean = {
    val meta = smi.asInstanceOf[ScriptInputStepMeta]

    if (meta.command.isEmpty) {
      logError("Command cannot be empty!")
    } else {
      logBasic(s"Running command: ${meta.command}")

      val rowMeta = Option(getInputRowMeta()).getOrElse(new RowMeta)
      smi.getFields(rowMeta, getStepname(), null, null, null)

      import scala.sys.process._

      val logger = ProcessLogger(
        s => {
          putRow(rowMeta, Array[Object](s))
          incrementLinesOutput()
        },
        e => logError(e)
      )

      val rc = meta.command.!(logger)
      if (rc != 0) {
        logError(s"Command returned non-zero RC: $rc")
        throw new KettleStepException(s"Command '${meta.command}' failed with non-zero RC: $rc")
      }
    }

    setOutputDone()
    false
  }
}


class ScriptInputStepMeta extends BaseStepMeta with StepMetaInterface {
  // If Kettle wants to live dangerously, I will, too!
  var command: String = ""
  var outputField: String = "output_line"

  def valueMeta() = new ValueMeta(outputField, ValueMetaInterface.TYPE_STRING)

  def getStep(smi: StepMeta, sdi: StepDataInterface, copyNr: Int, transMeta: TransMeta, trans: Trans) =
    new ScriptInputStep(smi, sdi, copyNr, transMeta, trans)

  def getStepData() = new ScriptInputStepData

  def setDefault(): Unit = { command = ""; outputField = "output_line" }

  override def getFields(inputRowMeta: RowMetaInterface, name: String, info: Array[RowMetaInterface], nextStep: StepMeta, space: VariableSpace): Unit = {
    val v = valueMeta()
    v.setOrigin(name)
    inputRowMeta.addValueMeta(v)
  }

  override def getXML() =
    s"<settings><command>${command}</command><outputField>${outputField}</outputField></settings>"

  override def loadXML(node: org.w3c.dom.Node, databases: java.util.List[DatabaseMeta], meta: org.pentaho.metastore.api.IMetaStore): Unit = loadXML(node, databases)
  override def loadXML(node: org.w3c.dom.Node, databases: java.util.List[DatabaseMeta], counters: java.util.Map[String, Counter]): Unit = loadXML(node, databases)

  override def loadXML(node: org.w3c.dom.Node, databases: java.util.List[DatabaseMeta]) = {
    println(s"Loading XML from $node")
    import javax.xml.xpath._
    val xpath = XPathFactory.newInstance.newXPath

    command = xpath.evaluate("//settings/command", node)
    outputField = xpath.evaluate("//settings/outputField", node)

    println(s"Loaded params: command = $command, outputField = $outputField")
  }
}


class ScriptInputStepData extends BaseStepData with StepDataInterface
