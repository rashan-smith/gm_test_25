package com.meter.giga.data.models.requests


import com.google.gson.annotations.SerializedName

data class TCPInfoRequestModel(
  @SerializedName("ATO")
  val aTO: Long?,
  @SerializedName("AdvMSS")
  val advMSS: Long?,
  @SerializedName("AppLimited")
  val appLimited: Long?,
  @SerializedName("Backoff")
  val backoff: Long?,
  @SerializedName("BusyTime")
  val busyTime: Long?,
  @SerializedName("BytesAcked")
  val bytesAcked: Long?,
  @SerializedName("BytesReceived")
  val bytesReceived: Long?,
  @SerializedName("BytesRetrans")
  val bytesRetrans: Long?,
  @SerializedName("BytesSent")
  val bytesSent: Long?,
  @SerializedName("CAState")
  val cAState: Long?,
  @SerializedName("DSackDups")
  val dSackDups: Long?,
  @SerializedName("DataSegsIn")
  val dataSegsIn: Long?,
  @SerializedName("DataSegsOut")
  val dataSegsOut: Long?,
  @SerializedName("Delivered")
  val delivered: Long?,
  @SerializedName("DeliveredCE")
  val deliveredCE: Long?,
  @SerializedName("DeliveryRate")
  val deliveryRate: Long?,
  @SerializedName("ElapsedTime")
  val elapsedTime: Long?,
  @SerializedName("Fackets")
  val fackets: Long?,
  @SerializedName("LastAckRecv")
  val lastAckRecv: Long?,
  @SerializedName("LastAckSent")
  val lastAckSent: Long?,
  @SerializedName("LastDataRecv")
  val lastDataRecv: Long?,
  @SerializedName("LastDataSent")
  val lastDataSent: Long?,
  @SerializedName("Lost")
  val lost: Long?,
  @SerializedName("MaxPacingRate")
  val maxPacingRate: Long?,
  @SerializedName("MinRTT")
  val minRTT: Long?,
  @SerializedName("NotsentBytes")
  val notsentBytes: Long?,
  @SerializedName("Options")
  val options: Long?,
  @SerializedName("PMTU")
  val pMTU: Long?,
  @SerializedName("PacingRate")
  val pacingRate: Long?,
  @SerializedName("Probes")
  val probes: Long?,
  @SerializedName("RTO")
  val rTO: Long?,
  @SerializedName("RTT")
  val rTT: Long?,
  @SerializedName("RTTVar")
  val rTTVar: Long?,
  @SerializedName("RWndLimited")
  val rWndLimited: Long?,
  @SerializedName("RcvMSS")
  val rcvMSS: Long?,
  @SerializedName("RcvOooPack")
  val rcvOooPack: Long?,
  @SerializedName("RcvRTT")
  val rcvRTT: Long?,
  @SerializedName("RcvSpace")
  val rcvSpace: Long?,
  @SerializedName("RcvSsThresh")
  val rcvSsThresh: Long?,
  @SerializedName("ReordSeen")
  val reordSeen: Long?,
  @SerializedName("Reordering")
  val reordering: Long?,
  @SerializedName("Retrans")
  val retrans: Long?,
  @SerializedName("Retransmits")
  val retransmits: Long?,
  @SerializedName("Sacked")
  val sacked: Long?,
  @SerializedName("SegsIn")
  val segsIn: Long?,
  @SerializedName("SegsOut")
  val segsOut: Long?,
  @SerializedName("SndBufLimited")
  val sndBufLimited: Long?,
  @SerializedName("SndCwnd")
  val sndCwnd: Long?,
  @SerializedName("SndMSS")
  val sndMSS: Long?,
  @SerializedName("SndSsThresh")
  val sndSsThresh: Long?,
  @SerializedName("SndWnd")
  val sndWnd: Long?,
  @SerializedName("State")
  val state: Long?,
  @SerializedName("TotalRetrans")
  val totalRetrans: Long?,
  @SerializedName("Unacked")
  val unacked: Long?,
  @SerializedName("WScale")
  val wScale: Long?
)
