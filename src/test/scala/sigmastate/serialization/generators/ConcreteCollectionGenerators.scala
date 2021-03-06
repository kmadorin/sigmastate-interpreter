package sigmastate.serialization.generators

import org.scalacheck.{Arbitrary, Gen}
import sigmastate.SInt
import sigmastate.Values.ConcreteCollection

trait ConcreteCollectionGenerators { self: ValueGeneratots =>

  val minCollLength = 1
  val maxCollLength = 10

  implicit val arbCCOfIntConstant: Arbitrary[ConcreteCollection[SInt.type]] = Arbitrary(intConstCollectionGen)

  val intConstCollectionGen: Gen[ConcreteCollection[SInt.type]] = for {
    size <- Gen.chooseNum(minCollLength, maxCollLength)
    listOfConstInts <- Gen.listOfN(size, intConstGen)
  } yield ConcreteCollection(listOfConstInts.toIndexedSeq)

}
