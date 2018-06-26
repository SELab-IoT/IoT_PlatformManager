package ConflictDetector.Converter.Interpreter

import ConflictDetector.Converter.Interpreter.Builder._
import ConflictDetector.Converter.SyntaxTree.RTree

abstract class GeneralRuleCombineInterpreter extends ICombineInterpreter[RTree] with IRTreeInterpreter {

  // Forward Mode 는 True로 보는 결과(Permit/Deny)를 의미
  // Reverse Mode 는 False인 결과(Deny/Permit 혹은 NA)를 의미
  // TODO: 나중에 좀 더 좋은 워딩이 생각나면 바로 바꾸기.
  def isForwardRule:RTree => Boolean = _.effect equals True

  // Permit = T 일 때는 PermitOverride
  // Deny = T 일 때는 DenyOverride
  def interpretForwardModeOverride(rules: RTree*):String = {
    //Case 2. Only Reverse Mode Rules ==> False
    val isAllFalse = !(rules exists isForwardRule)
    if(isAllFalse) FALSE
    else {
      //Case 1, 3. Only Forward Rules or Mixed ==> Disjunction Only Forward Mode Rules
      val forwardRules = rules filter isForwardRule
      val rs = forwardRules.map(rule => interpretRTree(rule.target, rule.condition, rule.effect))
      dis(rs:_*)
    }
  }

  // Permit = T 일 때는 DenyOverride
  // Deny = T 일 때는 PermitOverride
  def interpretReverseModeOverride(rules: RTree*):String = {
    //Case 1. Only Forward Mode ==> Disjunction All
    if(rules forall isForwardRule){
      val rs = rules.map(rule => interpretRTree(rule.target, rule.condition, rule.effect))
      dis(rs:_*)
    }
    //Case 2. Only Reversed Mode ==> False
    else if(!(rules exists isForwardRule)) FALSE
    //Case 3. !Combine(iFRs) & Combine(FRs)
    else {
      val forwardRules = rules filter isForwardRule
      val reverseRules = rules filterNot isForwardRule
      val invertedRules = reverseRules.map(rule => RTree(rule.target, rule.condition, True))
      con(neg(interpretReverseModeOverride(invertedRules:_*)), interpretReverseModeOverride(forwardRules:_*))
    }
  }

  // Permit = T 일 때는 Deny unless Permit
  // Deny = T 일 때는 Permit unless Deny
  def interpretReverseUnlessForward(rules: RTree*):String =
    //Case 1. Only Forward Mode ==> Disjunction All
    //Case 2. Only Reverse Mode ==> False
    //Case 3. Mixed ==> Disjunction All Forward Rules

    //Case 2
    if(!(rules exists isForwardRule)) FALSE
    //Case 1, 3
    else {
      val forwardRules = rules filter isForwardRule
      val rs = forwardRules.map(rule => interpretRTree(rule.target, rule.condition, rule.effect))
      dis(rs:_*)
    }


  // Permit = T 일 때는 Permit unless Deny
  // Deny = T 일 때는 Deny unless Permit
  def interpretForwardUnlessReverse(rules: RTree*):String =
    //Case 1. Only Forward Mode ==> True
    //Case 2. Only Reverse Mode ==> !(iFR1 | ... | iFRn)
    //Case 3. Mixed ==> !(iFR1 | ... | iFRn)

    //Case 1
    if(rules exists isForwardRule) TRUE
    //Case 2, 3
    else {
      val iFRs = rules.map(rule => interpretRTree(rule.target, rule.condition, True))
      neg(dis(iFRs:_*))
    }

}