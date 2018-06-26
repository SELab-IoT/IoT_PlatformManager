package ConflictDetector.Detector

import scala.xml.Elem
import ConflictDetector.Converter.Interpreter._
import ConflictDetector.Converter.Parser._

object Detector {

  def loadPolicy(path:String) = {
//    scala.xml.XML.load(path)
    TestPolicy.policy //추후 삭제 예정
  }
  def convertPolicy(value: Elem) = {
    val policies = XACMLParser.parseAll(value)
    //      2.1. PermitInterpreter에 넣고 돌린다. -> Boolean Form이 나온다.
    val permitSAT = PermitInterpreter.interpretAll(policies)
    //      2.2. DenyInterpreter에 넣고 돌린다. -> Boolean Form이 나온다.
    val denySAT = DenyInterpreter.interpretAll(policies)
    (permitSAT, denySAT)
  }

  def getSatisfiability(sat:String):Satisfiability = {
    if (sat equals Builder.FALSE) UnSAT //모든 경우에 대해 UnSAT함
    else if (sat equals Builder.TRUE) AllSAT //모든 경우에 대해 SAT함 - 추후 차집합 연산시 전체집합 U 라고 표기.
    else SomeSAT
  }

  def runSolverUntilGetUnSAT(expr:String, result:Satisfiability, rs:List[SolverResult]):List[SolverResult] =
    result match {
      case AllSAT => List(SolverResult(AllSAT, List.empty, List.empty))
      case UnSAT => rs
      case SomeSAT =>
        val solverResult = Solver.runSATSolver(expr)
        val nextExpr = expr + solverResult.buildCounterExampleClause()
        val nextResult = solverResult.satisfiable
        val nextRs = solverResult::rs
        runSolverUntilGetUnSAT(nextExpr, nextResult, nextRs)
    }


  def findAllSATCases(policyPath:String):(SatisfiableCaseSet, SatisfiableCaseSet)={

    //    1. 정책 파일을 로드한다.
    val policy = loadPolicy(policyPath)
    //    2. Converter에 넣고 돌린다.
    val (permitSAT, denySAT) = convertPolicy(policy)

    def findAllSATCases(sat:(String, Map[String, Long])):SatisfiableCaseSet= {
      val satisfiability = getSatisfiability(sat._1)
      //    3. 각 BooleanForm을 UnSAT이 나올 때 까지 Solver에 넣고 돌린다.
      val solverResults = runSolverUntilGetUnSAT(sat._1, satisfiability, List.empty)
      //    4. 각 반례들을 절-번호 맵핑테이블과 묶는다.
      SatisfiableCaseSet(solverResults, sat._2)
    }

    //    5. 최종 결과로, 정책에서 Permit이 나오는 모든 케이스와, Deny가 나오는 모든 케이스를 얻게된다.
    val permitSATCases = findAllSATCases(permitSAT)
    val denySATCases = findAllSATCases(denySAT)

    (permitSATCases, denySATCases)

  }

  def processConflictDetect() = {
//    A. 기존 정책 파일에 대해 2~5를 수행한다.
//      OldPolicy_permit: 기존 정책에서 Permit이 나오는 모든 케이스
//      OldPolicy_deny: 기존 정책에서 Deny가 나오는 모든 케이스
//    B. 새 정책 파일에 대해 2~5를 수행한다.
//      NewPolicy_permit: 새 정책에서 Permit이 나오는 모든 케이스
//      NewPolicy_deny: 새 정책에서 Deny가 나오는 모든 케이스
//    C. OldPolicy_permit, OldPolicy_deny, NewPolicy_permit, NewPolicy_deny에 대한 집합연산을 수행하여

  }

}
