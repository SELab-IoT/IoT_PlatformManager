package ConflictDetector.Converter.Interpreter

sealed trait Mode{
  def is(mode:String):Boolean
}

object Mode{
  def toMode(mode:String):Option[Mode] =
    if(Permit is mode) Some(Permit)
    else if(Deny is mode) Some(Deny)
    else None
}

case object Permit extends Mode {
  override def is(mode: String):Boolean = mode equalsIgnoreCase "Permit"
}
case object Deny extends Mode {
  override def is(mode: String):Boolean = mode equalsIgnoreCase "Deny"
}