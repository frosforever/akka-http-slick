package web

import akka.actor.ActorSystem
import akka.stream.{UniformFanInShape, ActorFlowMaterializer}
import akka.stream.scaladsl._
import org.scalatest.FunSuite

import scala.concurrent.Future

class PlayingWithFlows extends FunSuite {

  final case class Author(handle: String)

  final case class Hashtag(name: String)

  final case class Tweet(author: Author, timestamp: Long, body: String) {
    def hashtags: Set[Hashtag] =
      body.split(" ").collect { case t if t.startsWith("#") => Hashtag(t) }.toSet
  }

  val akka = Hashtag("#akka")

  implicit val system = ActorSystem("reactive-tweets")
  implicit val materializer = ActorFlowMaterializer()
//
//  val tweets: Source[Tweet, Unit] = ???
//
//
//  val authors: Source[Author, Unit] =
//    tweets
//      .filter(_.hashtags.contains(akka))
//      .map(_.author)
//
//  val hashtags: Source[Hashtag, Unit] = tweets.mapConcat(_.hashtags.toList)
//
//  val writeAuthors: Sink[Author, Unit] = ???
//  val writeHashtags: Sink[Hashtag, Unit] = ???
//
//
//
//
//  val g = FlowGraph.closed() { implicit b =>
//    import FlowGraph.Implicits._
//
//    val bcast = b.add(Broadcast[Tweet](2))
//    tweets ~> bcast.in
//    bcast.out(0) ~> Flow[Tweet].map(_.author.toString) ~> printSink
//    bcast.out(1) ~> Flow[Tweet].mapConcat(_.hashtags.map(_.toString).toList) ~> printSink
//  }

  val source = Source(1 to 10)
  val printSink = Sink.foreach(println)

  val flow = FlowGraph.closed() {implicit b =>
    import FlowGraph.Implicits._

    val bcast = b.add(Broadcast[Int](2))
    val merge = b.add(Merge[Int](2))
    source ~> bcast.in

    val doubledLess10 = bcast.out(0) ~> Flow[Int].map(_ * 10).filter(_ < 50)
    val regular = bcast.out(1).map{
      case n if n > 3 => throw new IllegalArgumentException()
      case n => n
    }

    doubledLess10 ~> merge.in(0)
    regular ~> merge.in(1)

    merge.out ~> printSink

  }

  test("flowing") {
    flow.run()
  }

//  test("another") {
//    val pairs = Source() { implicit b =>
//      import FlowGraph.Implicits._
//
//      // prepare graph elements
//      val zip = b.add(Zip[Int, Int]())
//      def ints = Source(() => Iterator.from(1))
//
//      // connect the graph
//      ints.filter(_ % 2 != 0) ~> zip.in0
//      ints.filter(_ % 2 == 0) ~> zip.in1
//
//      // expose port
//      zip.out
//    }
//  }

  test("somwthi") {
    val pickMaxOfThree = FlowGraph.partial() { implicit b =>
      import FlowGraph.Implicits._

      val zip1 = b.add(ZipWith[Int, Int, Int](math.max _))
      val zip2 = b.add(ZipWith[Int, Int, Int](math.max _))
      zip1.out ~> zip2.in0

      UniformFanInShape(zip2.out, zip1.in0, zip1.in1, zip2.in1)
    }

    val resultSink = Sink.head[Int]

    val g = FlowGraph.closed(resultSink) { implicit b =>
      sink =>
        import FlowGraph.Implicits._

        // importing the partial graph will return its shape (inlets & outlets)
        val pm3 = b.add(pickMaxOfThree)

        Source.single(1) ~> pm3.in(0)
        Source.single(2) ~> pm3.in(1)
        Source.single(3) ~> pm3.in(2)
        pm3.out ~> sink.inlet
    }

    val max: Future[Int] = g.run()
  }
}
