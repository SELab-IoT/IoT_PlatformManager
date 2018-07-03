package ConflictDetector.Detector

import java.lang.Long
import java.util.{ArrayList, HashMap, HashSet, List}
import scala.collection.immutable._

protected object JavaConverter {

  private def convertConflictSet(conflicts: Set[scala.List[scala.Long]])
    = conflicts.foldRight(new HashSet[List[Long]]())((i, acc) => {acc.add(i.foldRight(new ArrayList[Long]())((i, acc) => {acc.add(scala.Long.box(i)); acc})); acc})

  private def convertDictionary(dictionary: Map[scala.Long, String])
    = dictionary.map(t => (scala.Long.box(t._1), t._2)).foldRight(new HashMap[Long, String]())((i, acc) => {acc.put(i._1, i._2);acc})

  def convertToJavaResult(result: (Set[scala.List[scala.Long]], Map[scala.Long, String]))
    = Pair4J(convertConflictSet(result._1), convertDictionary(result._2))

}
