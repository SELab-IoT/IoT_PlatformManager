package XACML2Bool.Interpreter

import XACML2Bool.Interpreter.Builder._
import XACML2Bool.SyntaxTree._

/*
TODO: Policy/RuleInterpreter에 대해 각각 구현하고, 다 끝나면 DenyInterpreter 작성.
TODO: GeneralRuleCombineIntepreter를 계속 추가적으로 작성해야 함
TODO: 어쩌면 interpretPSTree는 다시 Interpreter로 옮겨갈 수도 있음.
(ps = PolicyInterpreter.interpretCombiningAlgorithm)
만약 PermitPolicyInterpreter와 DenyPolicyInterpreter가 완전 같다면 옮길것임.
*/
object PermitInterpreter extends Interpreter{

  override def interpretPermitRTree(target: Target, condition: CTree): String =
    PermitRuleInterpreter.interpretPermitRTree(target, condition)
  override def interpretDenyRTree(target: Target, condition: CTree): String =
    PermitRuleInterpreter.interpretDenyRTree(target, condition)

  def interpretPSTree(target: Target, policies: Combine[PTree]): String = {
    val t = interpretTarget(target)
    val ps = PermitPolicyInterpreter.interpretCombineAlgorithm(policies)
    if(ps equals FALSE) FALSE //target & False = False
    else if(ps equals TRUE) t //target & True = target
    else wrap(con(t, ps)) //target & Combine(policies)
  }

  def interpretPTree(target: Target, rules: Combine[RTree]): String = {
    val t = interpretTarget(target)
    val rs = PermitRuleInterpreter.interpretCombineAlgorithm(rules)
    if(rs equals FALSE) FALSE //target & False = False
    else if(rs equals TRUE) t //target & True = target
    else wrap(con(t, rs)) //target & Combine(rules)
  }

  //정책 조합
  object PermitPolicyInterpreter extends ICombineInterpreter[PTree] {

    def interpretPolicies(policies: PTree*) = policies.map(policy => interpretPTree(policy.target, policy.rules))

    override def True: Mode = Permit
    //Permit = T ==> Disjunction All
    override def interpretPO(policies: PTree*): String =
      dis(interpretPolicies(policies:_*):_*)
    //Permit = T ==> Conjunction All
    override def interpretDO(policies: PTree*): String =
      con(interpretPolicies(policies:_*):_*)
    //Permit = T ==> Disjunction All
    override def interpretDuP(policies: PTree*): String = ???
    //
    override def interpretPuD(policies: PTree*): String = ???
  }

  //규칙 조합
  object PermitRuleInterpreter extends GeneralRuleCombineInterpreter{

    override def True: Mode = Permit

    override def interpretPO(rules: RTree*): String = interpretForwardModeOverride(rules:_*)
    override def interpretDO(rules: RTree*): String = interpretReverseModeOverride(rules:_*)
    override def interpretDuP(rules: RTree*): String = interpretReverseUnlessForward(rules:_*)
    override def interpretPuD(rules: RTree*): String = interpretForwardUnlessReverse(rules:_*)

    //Permit = T => Target & Condition
    override def interpretPermitRTree(target: Target, condition: CTree): String = {
      val t = interpretTarget(target)
      val c = interpretCTree(condition)
      con(t, c)
    }
    //Permit = T => FALSE
    override def interpretDenyRTree(target: Target, condition: CTree): String = FALSE

  }

}