package com.vitorpamplona.amethyst.service.zaps

import com.vitorpamplona.amethyst.model.HexKey
import com.vitorpamplona.amethyst.model.Note
import com.vitorpamplona.amethyst.service.model.LnZapEventInterface
import com.vitorpamplona.amethyst.service.model.zaps.UserZaps
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal

class UserZapsTest {
  @Test
  fun nothing() {
    Assert.assertEquals(1, 1)
  }

  @Test
  fun user_without_zaps() {
    val actual = UserZaps.groupByUser(zaps = null)

    Assert.assertEquals(emptyList<Pair<Note, Note>>(), actual)
  }

  @Test
  fun avoid_duplicates_with_same_zap_request() {
    val zapRequest = mockk<Note>()

    val zaps: Map<Note, Note?> = mapOf(
      zapRequest to mockZapNoteWith("user-1", amount = 100),
      zapRequest to mockZapNoteWith("user-1", amount = 200),
    )

    val actual = UserZaps.groupByUser(zaps)

    Assert.assertEquals(1, actual.count())
    Assert.assertEquals(zapRequest, actual.first().first)
    Assert.assertEquals(
      BigDecimal(200),
      (actual.first().second.event as LnZapEventInterface).amount()
    )
  }

  @Test
  fun aggregate_zap_amount_group_by_user() {
    val zaps: Map<Note, Note?> = mapOf(
      mockk<Note>() to mockZapNoteWith("user-1", amount = 100),
      mockk<Note>() to mockZapNoteWith("user-1", amount = 200),
      mockk<Note>() to mockZapNoteWith("user-2", amount = 400),
    )

    val actual = UserZaps.groupByUser(zaps)

    Assert.assertEquals(2, actual.count())

    Assert.assertEquals(
      BigDecimal(300),
      (actual[0].second.event as LnZapEventInterface).amount()
    )

    Assert.assertEquals(
      BigDecimal(400),
      (actual[1].second.event as LnZapEventInterface).amount()
    )
  }

  private fun mockZapNoteWith(pubkey: HexKey, amount: Int): Note {

    val lnZapEvent = mockk<LnZapEventInterface>()
    every { lnZapEvent.amount() } returns amount.toBigDecimal()
    every { lnZapEvent.pubKey() } returns pubkey

    val zapNote = mockk<Note>()
    every { zapNote.event } returns lnZapEvent

    return zapNote
  }
}
