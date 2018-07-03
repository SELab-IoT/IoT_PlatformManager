package ConflictDetector.Detector

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Paths}

import scala.sys.process._

protected object Solver {

  def SAT = "% SATISFIABLE"
  def UNSAT = "% UNSATISFIABLE"

  val dir = "limboole/"
  val lib = dir + "limboole"
  val txt = dir + "sat"
  val opt = "-s"
  val command  = List(lib, opt, txt).reduce(_+" "+_)

  def writeTempFile(expr: String, path: String)= {
    Files.deleteIfExists(Paths.get(path))
    val writer = new PrintWriter(new File(path))
    writer.write(expr)
    writer.close()
  }

  def runSATSolver(expr: String):SolverResult = {
    writeTempFile(expr, txt)
//    println("comm:"+command)
    lazy val solver = command !!

    val lines = solver.toString.split("\n")
    val satisfiability = if(lines.head.contains(SAT)) SomeSAT else UnSAT
    val (ts, fs) = parseSatisfiableAssignment(lines.tail)
//    SolverResult(satisfiability, (ts ::: fs).sortWith((n1, n2) => Math.abs(n1) < Math.abs(n2)))
    SolverResult(satisfiability, (ts ::: fs).sorted)
  }

  def parseSatisfiableAssignment(satAss: Array[String]):(List[Long], List[Long])={
    val ass = satAss.map(_.split(" = ").map(_.trim))
    val ts = ass.filter(_.last equals "1").map(_.head.toLong).toList  // true : positive
    val fs = ass.filter(_.last equals "0").map(-_.head.toLong).toList // false: negative
    (ts, fs)
  }
}
