package com.simpleenergy.pentaho.ironio

import org.pentaho.di.core.Counter
import org.pentaho.di.core.database.DatabaseMeta
import org.pentaho.di.core.row._
import org.pentaho.di.core.variables.VariableSpace
import org.pentaho.di.trans._
import org.pentaho.di.trans.step._

import io.iron.ironmq._

class IronIOInputStep(smi: StepMeta, sdi: StepDataInterface, copyNr: Int, transMeta: TransMeta, trans: Trans) extends BaseStep(smi, sdi, copyNr, transMeta, trans) {
  def valOrNull(s: String) = if (s.isEmpty) null else s

  override def processRow(smi: StepMetaInterface, sdi: StepDataInterface): Boolean = {
    val meta = smi.asInstanceOf[IronIOInputStepMeta]

    if (meta.queue.isEmpty) {
      logError("Queue cannot be empty! Please provide a queue name.")
    } else {
      val rowMeta = Option(getInputRowMeta()).getOrElse(new RowMeta)
      smi.getFields(rowMeta, getStepname(), null, null, null)

      val client = new Client(valOrNull(meta.projectId), valOrNull(meta.token))

      val queue = client.queue(meta.queue)

      try {
        while (true) {
          val msg = queue.get()
          putRow(rowMeta, Array[Object](msg.getBody))
          queue.deleteMessage(msg)
          incrementLinesOutput()
        }
      } catch {
        case e: EmptyQueueException => // NOOP, just stop
      }
    }

    setOutputDone()
    false
  }
}


class IronIOInputStepMeta extends BaseStepMeta with StepMetaInterface {
  // If Kettle wants to live dangerously, I will, too!
  var token: String = ""
  var projectId: String = ""
  var queue: String = ""
  var outputField: String = "message"

  def valueMeta() = new ValueMeta(outputField, ValueMetaInterface.TYPE_STRING)

  def getStep(smi: StepMeta, sdi: StepDataInterface, copyNr: Int, transMeta: TransMeta, trans: Trans) =
    new IronIOInputStep(smi, sdi, copyNr, transMeta, trans)

  def getStepData() = new IronIOInputStepData

  def setDefault(): Unit = { token = ""; projectId = ""; queue = ""; outputField = "message" }

  override def getFields(inputRowMeta: RowMetaInterface, name: String, info: Array[RowMetaInterface], nextStep: StepMeta, space: VariableSpace): Unit = {
    val v = valueMeta()
    v.setOrigin(name)
    inputRowMeta.addValueMeta(v)
  }

  override def getXML() =
    s"<settings><token>${token}</token><projectId>${projectId}</projectId><queue>${queue}</queue><outputField>${outputField}</outputField></settings>"

  override def loadXML(node: org.w3c.dom.Node, databases: java.util.List[DatabaseMeta], meta: org.pentaho.metastore.api.IMetaStore): Unit = loadXML(node, databases)
  override def loadXML(node: org.w3c.dom.Node, databases: java.util.List[DatabaseMeta], counters: java.util.Map[String, Counter]): Unit = loadXML(node, databases)

  override def loadXML(node: org.w3c.dom.Node, databases: java.util.List[DatabaseMeta]) = {
    println(s"Loading XML from $node")
    import javax.xml.xpath._
    val xpath = XPathFactory.newInstance.newXPath

    token = xpath.evaluate("//settings/token", node)
    projectId = xpath.evaluate("//settings/projectId", node)
    queue = xpath.evaluate("//settings/queue", node)
    outputField = xpath.evaluate("//settings/outputField", node)

    println(s"Loaded params: token = $token, projectId = $projectId, queue = $queue, outputField = $outputField")
  }
}


class IronIOInputStepData extends BaseStepData with StepDataInterface
