package ConflictDetector.Converter.Interpreter

object TermSet{
  type Dictionary = Array[String]
  private var uuid:Long = 0
  private var terms:Dictionary = Array.empty
  def appendIfNotExist(term:String):Dictionary =
    if(terms contains term) terms
    else {
      terms = terms :+ term
      terms
    }
  def num(term:String):Long = (terms indexOf term) + 1
  def getTerms = terms
  def clear = terms = Array.empty

  override def toString: String = terms.toList.toString

}
