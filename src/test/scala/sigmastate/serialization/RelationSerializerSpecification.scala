package sigmastate.serialization

import sigmastate.Values._
import sigmastate._
import sigmastate.serialization.ValueSerializer._

class RelationSerializerSpecification extends TableSerializationSpecification {

  override val objects =
    Table(
      ("object", "bytes"),
      (LT(IntConstant(2), IntConstant(3)), Array[Byte](21, 11, 0, 0, 0, 0, 0, 0, 0, 2, 11, 0, 0, 0, 0, 0, 0, 0, 3)),
      (LE(IntConstant(2), IntConstant(3)), Array[Byte](22, 11, 0, 0, 0, 0, 0, 0, 0, 2, 11, 0, 0, 0, 0, 0, 0, 0, 3)),
      (GT(IntConstant(6), IntConstant(5)), Array[Byte](23, 11, 0, 0, 0, 0, 0, 0, 0, 6, 11, 0, 0, 0, 0, 0, 0, 0, 5)),
      (GE(IntConstant(6), IntConstant(5)), Array[Byte](24, 11, 0, 0, 0, 0, 0, 0, 0, 6, 11, 0, 0, 0, 0, 0, 0, 0, 5)),
      (EQ(TrueLeaf, FalseLeaf), Array[Byte](25, 12, 13)),
      (NEQ(TrueLeaf, FalseLeaf), Array[Byte](26, 12, 13))
    )

  tableRoundTripTest("Relations: serializer round trip")
  tablePredefinedBytesTest("Relations: deserialize from predefined bytes")

  property("Relations: serialization LT(bool, bool) must fail") {
    assertThrows[Error] {
      deserialize(Array[Byte](21, 12, 13))
    }
  }
}
