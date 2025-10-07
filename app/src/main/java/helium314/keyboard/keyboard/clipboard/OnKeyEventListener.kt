// SPDX-License-Identifier: GPL-3.0-only

package com.keyfluent.keyboard.keyboard.clipboard

interface OnKeyEventListener {

    fun onKeyDown(clipId: Long)

    fun onKeyUp(clipId: Long)

}