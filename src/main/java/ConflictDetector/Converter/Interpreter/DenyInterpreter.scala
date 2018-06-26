package ConflictDetector.Converter.Interpreter

import ConflictDetector.Converter.Interpreter.Builder._
import ConflictDetector.Converter.SyntaxTree._

object DenyInterpreter extends Interpreter{

  override def interpretPTree(target: Target, rules: Combine[RTree]): String =
    DenyPolicyInterpreter.interpretPTree(target, rules)

  override def interpretPermitRTree(target: Target, condition: CTree): String =
    DenyRuleInterpreter.interpretPermitRTree(target, condition)
  override def interpretDenyRTree(target: Target, condition: CTree): String =
    DenyRuleInterpreter.interpretDenyRTree(target, condition)

  def interpretPSTree(target: Target, policies: Combine[PTree]): String = {
    val t = interpretTarget(target)
    val ps = DenyPolicyInterpreter.interpretCombineAlgorithm(policies)
    con(t, ps) //target & Combine(policies)
  }

  //정책 조합
  object DenyPolicyInterpreter extends GeneralPolicyCombineInterpreter {

    override def True: Mode = Deny

    override def interpretPO(policies: PTree*): String =
      conjunctionAll(policies:_*)
    override def interpretDO(policies: PTree*): String =
      disjunctionAll(policies:_*)
    override def interpretDuP(policies: PTree*): String =
      conjunctionAll(policies:_*)
    override def interpretPuD(policies: PTree*): String =
      disjunctionAll(policies:_*)

    override def interpretPTree(target: Target, rules: Combine[RTree]): String = {
      val t = interpretTarget(target)
      val rs = DenyRuleInterpreter.interpretCombineAlgorithm(rules)
      con(t, rs)
    }

  }

  //규칙 조합
  object DenyRuleInterpreter extends GeneralRuleCombineInterpreter{

    override def True: Mode = Deny

    override def interpretPO(rules: RTree*): String = interpretReverseModeOverride(rules:_*)
    override def interpretDO(rules: RTree*): String = interpretForwardModeOverride(rules:_*)
    override def interpretDuP(rules: RTree*): String = interpretForwardUnlessReverse(rules:_*)
    override def interpretPuD(rules: RTree*): String = interpretReverseUnlessForward(rules:_*)

    override def interpretPermitRTree(target: Target, condition: CTree): String = FALSE
    override def interpretDenyRTree(target: Target, condition: CTree): String = {
      val t = interpretTarget(target)
      val c = interpretCTree(condition)
      con(t, c)
    }

  }

}