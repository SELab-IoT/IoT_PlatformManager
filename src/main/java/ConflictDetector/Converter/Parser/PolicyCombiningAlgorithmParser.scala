package ConflictDetector.Converter.Parser

import ConflictDetector.Converter.SyntaxTree.PTree

object PolicyCombiningAlgorithmParser extends ICombiningAlgorithmParser[PTree] {
  val AlgIdAttribute = "PolicyCombiningAlgId"
  val PermitOverride = CombiningAlgorithm("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-overrides")
  val DenyOverride = CombiningAlgorithm("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-overrides")
  val DenyUnlessPermit = CombiningAlgorithm("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-unless-permit")
  val PermitUnlessDeny = CombiningAlgorithm("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-unless-deny")
  val NoSuchAlgorithmID = "No Such Policy Combining Algorithm"
}