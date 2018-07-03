package ConflictDetector.Detector

protected sealed trait Satisfiability
protected case object AllSAT extends Satisfiability
protected case object UnSAT extends Satisfiability
protected case object SomeSAT extends Satisfiability