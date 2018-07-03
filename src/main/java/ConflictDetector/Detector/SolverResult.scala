package ConflictDetector.Detector

protected case class SolverResult(satisfiable:Satisfiability, assigns:List[Long]) {
  def buildCounterExampleClause():String =
    "\n & !(" + assigns.map(t => (if (t>0) t else "!" + (-t)).toString).reduce(_ + " & " + _) + ")"
  def getSATAssigns():List[java.lang.Long] = assigns.map(Long.box(_))
  override def toString: String = assigns.toString
}
