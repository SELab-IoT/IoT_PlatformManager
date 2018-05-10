package XACML2Bool.Interpreter

import XACML2Bool.Interpreter.Builder.dis
import XACML2Bool.SyntaxTree.RTree

abstract class GeneralRuleCombineInterpreter extends ICombineInterpreter[RTree] with IRTreeInterpreter {
  // Forward Mode 는 True로 보는 결과(Permit/Deny)를 의미
  // Reverse Mode 는 False인 결과(Deny/Permit 혹은 NA)를 의미
  def isForwardRule:RTree => Boolean = _.effect equals True

  // Permit = T 일 때는 PermitOverride
  // Deny = T 일 때는 DenyOverride
  def interpretForwardModeOverrideRules(rules: RTree*):String = {
    //Case 2. Only Reverse Mode Rules ==> False
    val isAllFalse = !(rules exists isForwardRule)
    if(isAllFalse) ""
    else {
      //Case 1, 3. Only Forward Rules or Mixed ==> Disjunction Only Forward Mode Rules
      val rs = rules.filter(isForwardRule)
        .map(rule => interpretRTree(rule.target, rule.condition, rule.effect))
      dis(rs:_*)
    }
  }

  // Permit = T 일 때는 DenyOverride
  // Deny = T 일 때는 PermitOverride
  def interpretReverseModeOverrideRules(rules: RTree*):String = {
    //Case 1. Only Forward Mode ==> Disjunction All
    if(rules forall isForwardRule){
      val rs = rules.map(rule => interpretRTree(rule.target, rule.condition, rule.effect))
      dis(rs:_*)
    }
    //Case 2. Only Reversed Mode ==> False
    else if(!(rules exists isForwardRule)) ""
    //Case 3. !Combine(iPRs) & Combine(PRs)
    else {
      val reverseRules = rules filterNot isForwardRule
      val forwardRules = rules filter isForwardRule
      ???
    }
  }
}