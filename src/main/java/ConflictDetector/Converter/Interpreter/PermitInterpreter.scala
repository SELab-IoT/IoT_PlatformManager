package ConflictDetector.Converter.Interpreter

import ConflictDetector.Converter.Interpreter.Builder._
import ConflictDetector.Converter.SyntaxTree._

object PermitInterpreter extends Interpreter{

  override def interpretPTree(target: Target, rules: Combine[RTree]): String =
    PermitPolicyInterpreter.interpretPTree(target, rules)

  override def interpretPermitRTree(target: Target, condition: CTree): String =
    PermitRuleInterpreter.interpretPermitRTree(target, condition)
  override def interpretDenyRTree(target: Target, condition: CTree): String =
    PermitRuleInterpreter.interpretDenyRTree(target, condition)

  def interpretPSTree(target: Target, policies: Combine[PTree]): String = {
    val t = interpretTarget(target)
    val ps = PermitPolicyInterpreter.interpretCombineAlgorithm(policies)
    con(t, ps) //target & Combine(policies)
  }

  //정책 조합
  object PermitPolicyInterpreter extends GeneralPolicyCombineInterpreter {

    override def True: Mode = Permit
    //Permit = T ==> Disjunction All
    override def interpretPO(policies: PTree*): String =
      disjunctionAll(policies:_*)
    override def interpretDO(policies: PTree*): String =
      conjunctionAll(policies:_*)
    override def interpretDuP(policies: PTree*): String =
      disjunctionAll(policies:_*)
    override def interpretPuD(policies: PTree*): String =
      conjunctionAll(policies:_*)

    override def interpretPTree(target: Target, rules: Combine[RTree]): String = {
      val t = interpretTarget(target)
      val rs = PermitRuleInterpreter.interpretCombineAlgorithm(rules)
      con(t, rs) //target & Combine(rules)
    }

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