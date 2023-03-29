package com.bynder.mo.http.utils

import java.util.UUID

final case class JWTPayload(accountId: UUID, userId: String)
