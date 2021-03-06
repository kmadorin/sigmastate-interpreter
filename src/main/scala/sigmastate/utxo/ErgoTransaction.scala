package sigmastate.utxo

import com.google.common.primitives.Bytes
import scorex.crypto.authds.ADKey
import scorex.crypto.hash.{Blake2b256, Digest32}
import sigmastate.UncheckedTree
import sigmastate.interpreter.ProverResult

import scala.util.Try


trait ErgoBoxReader {
  def byId(boxId: ADKey): Try[ErgoBox]
}

trait InputTemplate {
  val boxId: ADKey
}


case class UnsignedInput(override val boxId: ADKey) extends InputTemplate

case class Input(override val boxId: ADKey, spendingProof: ProverResult[UncheckedTree]) extends InputTemplate {
  def bytes: Array[Byte] = Array()
}

trait ErgoTransactionTemplate[IT <: InputTemplate] {
  val inputs: IndexedSeq[IT]
  val outputCandidates: IndexedSeq[ErgoBoxCandidate]

  require(outputCandidates.size <= Short.MaxValue)

  lazy val outputs = outputCandidates.indices.map(idx => outputCandidates(idx).toBox(id, idx.toShort))

  //todo: move to some util class
  def concatBytes(seq: Traversable[Array[Byte]]): Array[Byte] = {
    val length: Int = seq.map(_.length).sum
    val result: Array[Byte] = new Array[Byte](length)
    var pos: Int = 0
    seq.foreach{ array =>
      System.arraycopy(array, 0, result, pos, array.length)
      pos += array.length
    }
    result
  }

  lazy val messageToSign: Array[Byte] =
    Bytes.concat(if (outputCandidates.nonEmpty) concatBytes(outputCandidates.map(_.bytesWithNoRef)) else Array[Byte](),
      concatBytes(inputs.map(_.boxId)))

  lazy val id: Digest32 = Blake2b256.hash(messageToSign)
}

case class UnsignedErgoTransaction(override val inputs: IndexedSeq[UnsignedInput],
                                   override val outputCandidates: IndexedSeq[ErgoBoxCandidate])
  extends ErgoTransactionTemplate[UnsignedInput] {

  def toSigned(proofs: IndexedSeq[ProverResult[UncheckedTree]]): ErgoTransaction = {
    require(proofs.size == inputs.size)
    val ins = inputs.zip(proofs).map{case (ui, proof) => Input(ui.boxId, proof)}
    ErgoTransaction(ins, outputCandidates)
  }
}

/**
  * Fully signed transaction
  * @param inputs
  * @param outputCandidates
  */
case class ErgoTransaction(override val inputs: IndexedSeq[Input],
                           override val outputCandidates: IndexedSeq[ErgoBoxCandidate])
  extends ErgoTransactionTemplate[Input]