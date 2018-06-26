package ConflictDetector.Detector

case class SatisfiableCaseSet(clauses:List[SolverResult], indexMap: Map[String, Long])