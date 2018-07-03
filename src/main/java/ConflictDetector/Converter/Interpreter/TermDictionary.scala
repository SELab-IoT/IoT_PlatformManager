package ConflictDetector.Converter.Interpreter

object TermDictionary{
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

  def clear = terms = Array.empty

  override def toString: String = terms.toList.toString

  //i starts from 1
  def getTerm(i:Long)  = terms.apply(i.toInt - 1)
  def toMap: Map[Long, String] = terms.zipWithIndex.toMap.map(t => (t._2.toLong + 1, t._1))
  def lastIndex = terms.length

}
