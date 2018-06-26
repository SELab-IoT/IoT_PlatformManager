package ConflictDetector.Detector

case class SolverResult(satisfiable:Satisfiability, trueAssigns:List[String], falseAssigns:List[String]) {
  def buildCounterExampleClause():String = {
    val ass = trueAssigns:::falseAssigns.map("!"+_)
    "\n & !(" + ass.reduce(_+" & "+_) + ")"
  }
}
