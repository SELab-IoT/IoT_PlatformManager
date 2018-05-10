package XACML2Bool.Parser

import XACML2Bool.Parser.Parser.ParseException
import XACML2Bool.SyntaxTree._

trait CombiningAlgorithmParser{
  def AlgID:String
  def PermitOverride:String
  def DenyOverride:String
  def DenyUnlessPermit:String
  def PermitUnlessDeny:String
  def NoSuchAlgorithmID:String
  def parseCombiningAlgorithm(combAlg: String, terms: TTree*): Combine[TTree] =
    combAlg match {
      case PermitOverride   =>  PO(terms:_*)
      case DenyOverride     =>  DO(terms:_*)
      case PermitUnlessDeny =>  DuP(terms:_*)
      case DenyUnlessPermit =>  PuD(terms:_*)
      case _ => throw new ParseException(NoSuchAlgorithmID)
    }
}

object PolicyCombiningAlgorithmParser extends CombiningAlgorithmParser {
  def AlgID = "PolicyCombiningAlgId"
  def PermitOverride = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-overrides"
  def DenyOverride = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-overrides"
  def DenyUnlessPermit = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-unless-permit"
  def PermitUnlessDeny = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-unless-deny"
  def NoSuchAlgorithmID = "No Such Policy Combining Algorithm"
}

object RuleCombiningAlgorithmParser extends CombiningAlgorithmParser {
  def AlgID = "RuleCombiningAlgId"
  def PermitOverride = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides"
  def DenyOverride = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-overrides"
  def DenyUnlessPermit = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit"
  def PermitUnlessDeny = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-unless-deny"
  def NoSuchAlgorithmID = "No Such Rule Combining Algorithm"
}