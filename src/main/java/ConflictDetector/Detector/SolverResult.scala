package ConflictDetector.Detector

protected case class SolverResult(satisfiable:Satisfiability, assigns:List[Long]) {
  def buildCounterExampleClause():String =
    "\n & !(" + assigns.map(t => (if (t>0) t else "!" + (-t)).toString).reduce(_ + " & " + _) + ")"
//    "\n & !(" + assigns.filter(_>0).map(_.toString).reduce(_ + " & " + _) + ")" // False 텀을 아얘 무시하는 방법.
  def getSATAssigns():List[java.lang.Long] = assigns.map(Long.box(_))
  override def toString: String = assigns.toString
}
