package ConflictDetector.Detector

sealed trait Satisfiability
case object AllSAT extends Satisfiability
case object UnSAT extends Satisfiability
case object SomeSAT extends Satisfiability