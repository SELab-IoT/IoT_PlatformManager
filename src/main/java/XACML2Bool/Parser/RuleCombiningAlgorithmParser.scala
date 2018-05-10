package XACML2Bool.Parser

import XACML2Bool.SyntaxTree.RTree

object RuleCombiningAlgorithmParser extends ICombiningAlgorithmParser[RTree] {
  val AlgIdAttribute = "RuleCombiningAlgId"
  val PermitOverride = CombiningAlgorithm("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides")
  val DenyOverride = CombiningAlgorithm("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-overrides")
  val DenyUnlessPermit = CombiningAlgorithm("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit")
  val PermitUnlessDeny = CombiningAlgorithm("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-unless-deny")
  val NoSuchAlgorithmID = "No Such Rule Combining Algorithm"
}