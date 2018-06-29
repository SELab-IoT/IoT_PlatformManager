package ConflictDetector.Detector

import scala.xml.Elem
import ConflictDetector.Converter.Interpreter._
import ConflictDetector.Converter.Parser._

object Detector {

  private type SatisfiableCaseSet = Set[SolverResult]

  //TODO: 추후 실제 파일 로드하게끔 수정 예정
  def loadPolicy(path:String) = {
//    scala.xml.XML.load(path)
    //아래는 추후 삭제.
    path match {
      case "originalPolicy" => TestPolicy.originalPolicy
      case "modifiedPolicy" => TestPolicy.modifiedPolicy
    }
  }
  def parsePolicy(value: Elem) = {
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

  def runSolverUntilGetUnSAT(expr:String, result:Satisfiability, rs:Set[SolverResult]):Set[SolverResult] =
    result match {
      case AllSAT => Set(SolverResult(AllSAT, List.empty))
      case UnSAT => rs
      case SomeSAT =>
        val solverResult = Solver.runSATSolver(expr)
        //        println("Solve: "+expr+" => "+solverResult)

        if (solverResult.satisfiable equals UnSAT) rs
        else {
          val nextExpr = expr + solverResult.buildCounterExampleClause()
          val nextResult = solverResult.satisfiable
          val nextRs = rs + solverResult
          runSolverUntilGetUnSAT(nextExpr, nextResult, nextRs)
        }

    }

  def convertPolicy(policyPath:String) =
    parsePolicy(loadPolicy(policyPath))

  def findAllSATCases(sat:String):SatisfiableCaseSet= {
    val satisfiability = getSatisfiability(sat)
    // 3. 각 BooleanForm을 UnSAT이 나올 때 까지 Solver에 넣고 돌린다.
    val solverResults = runSolverUntilGetUnSAT(sat, satisfiability, Set.empty)

    // 4. 집합 연산을 위한 Don't Care 텀 추가
    val results = solverResults.flatMap(sr => {
      def duplicate(ass:List[Long], i:Long):List[List[Long]] = {
        if(i==0) List(ass.sorted)
        else if(!ass.exists(Math.abs(_)==i)){
          val ts = duplicate(i::ass, i-1)
          val fs = duplicate(-i::ass, i-1)
          ts ::: fs
        }
        else duplicate(ass, i-1)
      }
      duplicate(sr.assigns, TermSet.getTerms.length+1).map(SolverResult(SomeSAT, _))
    })
    results
  }

  def removeNA(permitSAT:SatisfiableCaseSet, denySAT: SatisfiableCaseSet):(SatisfiableCaseSet, SatisfiableCaseSet) =
    (permitSAT diff denySAT, denySAT diff permitSAT)


  def findPermitAndDenySATCases(permitSAT:String, denySAT:String)={
    val naPermit = findAllSATCases(permitSAT)
    val naDeny = findAllSATCases(denySAT)
    removeNA(naPermit, naDeny)
  }

  def processConflictDetect = {
//    A. 기존 정책 파일에 대해 2~5를 수행한다.
//      OldPolicy_permit: 기존 정책에서 Permit이 나오는 모든 케이스
//      OldPolicy_deny: 기존 정책에서 Deny가 나오는 모든 케이스
//    B. 새 정책 파일에 대해 2~5를 수행한다.
//      NewPolicy_permit: 새 정책에서 Permit이 나오는 모든 케이스
//      NewPolicy_deny: 새 정책에서 Deny가 나오는 모든 케이스
//    C. OldPolicy_permit, OldPolicy_deny, NewPolicy_permit, NewPolicy_deny에 대한 집합연산을 수행하여 ... ?

    //TODO: 수정전/후 정책파일 이름. 추후 인자로 받게끔 수정 예정
    val originalPolicy = "originalPolicy"
    val modifiedPolicy = "modifiedPolicy"

    //정책 파일 로드 후 파싱, Term Dictionary도 작성한다.
    TermSet.clear
    val (oldPermitSAT, oldDenySAT) = convertPolicy(originalPolicy)
    val (newPermitSAT, newDenySAT) = convertPolicy(modifiedPolicy)

    //모든 Satisfiable Cases를 찾아낸다. NA는 최대한 제거한다.
    // * parsePolicy가 side-effect free 하지 않아 아래와 묶을 수 없었다.
    val (oldPermit, oldDeny) = findPermitAndDenySATCases(oldPermitSAT, oldDenySAT)
    val (newPermit, newDeny) = findPermitAndDenySATCases(newPermitSAT, newDenySAT)

    //차집합 연산을 통해 4종류의 충돌을 찾아낸다.
    val permitButNotPermit = oldPermit diff newPermit
    val denyButNotDeny = oldDeny diff newDeny
    val notPermitButPermit = newPermit diff oldPermit
    val notDenyButDeny = newDeny diff oldDeny

    //TODO: 결과 출력 - 추후 리턴으로 넘기게끔 수정 예정
    println("Dictionary: "+TermSet)
    println("Op : "+oldPermit)
    println("Np : "+newPermit)
    println("Od : "+oldDeny)
    println("Nd : "+newDeny)
    println("Conflict 1. 수정 전에 Permit이 나오는 케이스였으나 수정 후 Permit이 아니게 된 경우")
    println("Op - Np : "+permitButNotPermit)
    println("Conflict 2. 수정 전에 Deny가 나오는 케이스였으나 수정 후 Deny가 아니게 된 경우")
    println("Od - Nd : "+denyButNotDeny)
    println("Conflict 3. 수정 전에 Permit이 나오지 않는 케이스였으나 수정 후 Permit이 나오게 된 경우")
    println("Np - Op : "+notPermitButPermit)
    println("Conflict 4. 수정 전에 Deny가 나오지 않는 케이스였으나 수정 후 Deny가 나오게 된 경우")
    println("Nd - Od : "+notDenyButDeny)


  }

}