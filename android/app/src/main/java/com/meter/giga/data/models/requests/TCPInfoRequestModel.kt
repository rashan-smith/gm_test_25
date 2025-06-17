package com.meter.giga.data.models.requests


import com.google.gson.annotations.SerializedName

data class TCPInfoRequestModel(
    @SerializedName("ATO")
    val aTO: Int?,
    @SerializedName("AdvMSS")
    val advMSS: Int?,
    @SerializedName("AppLimited")
    val appLimited: Int?,
    @SerializedName("Backoff")
    val backoff: Int?,
    @SerializedName("BusyTime")
    val busyTime: Int?,
    @SerializedName("BytesAcked")
    val bytesAcked: Int?,
    @SerializedName("BytesReceived")
    val bytesReceived: Int?,
    @SerializedName("BytesRetrans")
    val bytesRetrans: Int?,
    @SerializedName("BytesSent")
    val bytesSent: Int?,
    @SerializedName("CAState")
    val cAState: Int?,
    @SerializedName("DSackDups")
    val dSackDups: Int?,
    @SerializedName("DataSegsIn")
    val dataSegsIn: Int?,
    @SerializedName("DataSegsOut")
    val dataSegsOut: Int?,
    @SerializedName("Delivered")
    val delivered: Int?,
    @SerializedName("DeliveredCE")
    val deliveredCE: Int?,
    @SerializedName("DeliveryRate")
    val deliveryRate: Int?,
    @SerializedName("ElapsedTime")
    val elapsedTime: Int?,
    @SerializedName("Fackets")
    val fackets: Int?,
    @SerializedName("LastAckRecv")
    val lastAckRecv: Int?,
    @SerializedName("LastAckSent")
    val lastAckSent: Int?,
    @SerializedName("LastDataRecv")
    val lastDataRecv: Int?,
    @SerializedName("LastDataSent")
    val lastDataSent: Int?,
    @SerializedName("Lost")
    val lost: Int?,
    @SerializedName("MaxPacingRate")
    val maxPacingRate: Int?,
    @SerializedName("MinRTT")
    val minRTT: Int?,
    @SerializedName("NotsentBytes")
    val notsentBytes: Int?,
    @SerializedName("Options")
    val options: Int?,
    @SerializedName("PMTU")
    val pMTU: Int?,
    @SerializedName("PacingRate")
    val pacingRate: Int?,
    @SerializedName("Probes")
    val probes: Int?,
    @SerializedName("RTO")
    val rTO: Int?,
    @SerializedName("RTT")
    val rTT: Int?,
    @SerializedName("RTTVar")
    val rTTVar: Int?,
    @SerializedName("RWndLimited")
    val rWndLimited: Int?,
    @SerializedName("RcvMSS")
    val rcvMSS: Int?,
    @SerializedName("RcvOooPack")
    val rcvOooPack: Int?,
    @SerializedName("RcvRTT")
    val rcvRTT: Int?,
    @SerializedName("RcvSpace")
    val rcvSpace: Int?,
    @SerializedName("RcvSsThresh")
    val rcvSsThresh: Int?,
    @SerializedName("ReordSeen")
    val reordSeen: Int?,
    @SerializedName("Reordering")
    val reordering: Int?,
    @SerializedName("Retrans")
    val retrans: Int?,
    @SerializedName("Retransmits")
    val retransmits: Int?,
    @SerializedName("Sacked")
    val sacked: Int?,
    @SerializedName("SegsIn")
    val segsIn: Int?,
    @SerializedName("SegsOut")
    val segsOut: Int?,
    @SerializedName("SndBufLimited")
    val sndBufLimited: Int?,
    @SerializedName("SndCwnd")
    val sndCwnd: Int?,
    @SerializedName("SndMSS")
    val sndMSS: Int?,
    @SerializedName("SndSsThresh")
    val sndSsThresh: Int?,
    @SerializedName("SndWnd")
    val sndWnd: Int?,
    @SerializedName("State")
    val state: Int?,
    @SerializedName("TotalRetrans")
    val totalRetrans: Int?,
    @SerializedName("Unacked")
    val unacked: Int?,
    @SerializedName("WScale")
    val wScale: Int?
)
