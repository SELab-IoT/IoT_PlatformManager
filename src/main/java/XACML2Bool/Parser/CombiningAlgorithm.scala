package XACML2Bool.Parser

class CombiningAlgorithm(val algorithmId:String) extends AnyVal
object CombiningAlgorithm{
  implicit def apply(algorithmId: String): CombiningAlgorithm = new CombiningAlgorithm(algorithmId)
  def unapply(alg: CombiningAlgorithm): Option[String] = Some(alg.algorithmId)
}