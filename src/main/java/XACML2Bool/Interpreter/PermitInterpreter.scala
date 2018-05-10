package XACML2Bool.Interpreter

import XACML2Bool.SyntaxTree._

object PermitInterpreter extends Interpreter{

  override def True: Mode = Permit

  override def interpretPermitRule(target:Target, condition:CTree):String = ???
  override def interpretDenyRule(target:Target, condition:CTree):String = ???

  override def interpretPOPolicies(policies: PTree*): String = ???
  override def interpretDOPolicies(policies: PTree*): String = ???
  override def interpretDuPPolicies(policies: PTree*): String = ???
  override def interpretPuDPolicies(policies: PTree*): String = ???

  override def interpretPORules(rules: RTree*): String = {
    /*
    Case 1: Only Permit Rules in Seq
    -> PR1 | ... | PRn
    Case 2: Only Deny Rules in Seq
    -> False
    Case 3: Mixed
    -> PR1 | ... | PRn
    */

    //Case 2
    val isAllFalse = !rules.exists(_.effect equals True)
    if(isAllFalse) ""
    else {
      //      rules.map(rule => interpretRTree(???))
      ???
    }
  }
  override def interpretDORules(rules: RTree*): String = ???
  override def interpretDuPRules(rules: RTree*): String = ???
  override def interpretPuDRules(rules: RTree*): String = ???
}
