/**
 * Copyright (c) 2024 Vitor Pamplona
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.vitorpamplona.quartz.nip17Dm.messages

import androidx.compose.runtime.Immutable
import com.vitorpamplona.quartz.nip01Core.EventHintBundle
import com.vitorpamplona.quartz.nip01Core.core.HexKey
import com.vitorpamplona.quartz.nip01Core.core.TagArrayBuilder
import com.vitorpamplona.quartz.nip01Core.signers.eventTemplate
import com.vitorpamplona.quartz.nip01Core.tags.events.ETag
import com.vitorpamplona.quartz.nip01Core.tags.people.PTag
import com.vitorpamplona.quartz.nip17Dm.base.BaseDMGroupEvent
import com.vitorpamplona.quartz.nip17Dm.files.ChatMessageEncryptedFileHeaderEvent.Companion.ALT_DESCRIPTION
import com.vitorpamplona.quartz.nip31Alts.alt
import com.vitorpamplona.quartz.utils.TimeUtils

@Immutable
class ChatMessageEvent(
    id: HexKey,
    pubKey: HexKey,
    createdAt: Long,
    tags: Array<Array<String>>,
    content: String,
    sig: HexKey,
) : BaseDMGroupEvent(id, pubKey, createdAt, KIND, tags, content, sig) {
    fun replyTo() = tags.mapNotNull(ETag::parseId)

    companion object {
        const val KIND = 14
        const val ALT = "Direct message"

        fun build(
            msg: String,
            to: List<PTag>,
            createdAt: Long = TimeUtils.now(),
            initializer: TagArrayBuilder<ChatMessageEvent>.() -> Unit = {},
        ) = eventTemplate(KIND, msg, createdAt) {
            alt(ALT_DESCRIPTION)
            group(to)
            initializer()
        }

        fun reply(
            msg: String,
            reply: EventHintBundle<ChatMessageEvent>,
            createdAt: Long = TimeUtils.now(),
            initializer: TagArrayBuilder<ChatMessageEvent>.() -> Unit = {},
        ) = eventTemplate(KIND, msg, createdAt) {
            alt(ALT_DESCRIPTION)
            group((reply.event.recipients() + reply.toPTag()).distinctBy { it.pubKey })
            initializer()
        }
    }
}
