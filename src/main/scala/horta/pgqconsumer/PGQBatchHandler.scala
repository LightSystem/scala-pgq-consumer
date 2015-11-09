package horta.pgqconsumer

import com.brandwatch.pgqconsumer.PGQEvent

/**
 * Created by horta on 09-11-2015.
 */
trait PGQBatchHandler {
  def handleBatch(batch: List[PGQEvent])
}
