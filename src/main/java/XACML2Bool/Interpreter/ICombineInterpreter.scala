package XACML2Bool.Interpreter

import XACML2Bool.SyntaxTree._

trait ICombineInterpreter {

  def True:Mode

  def interpretPOPolicies(policies: PTree*):String
  def interpretDOPolicies(policies: PTree*):String
  def interpretDuPPolicies(policies: PTree*):String
  def interpretPuDPolicies(policies: PTree*):String

  def interpretPORules(rules: RTree*):String
  def interpretDORules(rules: RTree*):String
  def interpretDuPRules(rules: RTree*):String
  def interpretPuDRules(rules: RTree*):String

  def interpretCombinePolicies(policies: Combine[PTree]):String =
    policies match {
      case PO(ps @ _*) => interpretPOPolicies(ps:_*)
      case DO(ps @ _*) => interpretDOPolicies(ps:_*)
      case DuP(ps @ _*) => interpretDuPPolicies(ps:_*)
      case PuD(ps @ _*) => interpretPuDPolicies(ps:_*)
    }

  def interpretCombineRules(rules: Combine[RTree]):String =
    rules match {
      case PO(rs @ _*) => interpretPORules(rs:_*)
      case DO(rs @ _*) => interpretDORules(rs:_*)
      case DuP(rs @ _*) => interpretDuPRules(rs:_*)
      case PuD(rs @ _*) => interpretPuDRules(rs:_*)
    }

}
