package com.meter.giga.utils

import com.meter.giga.data.models.requests.BBRInfoRequestModel
import com.meter.giga.data.models.requests.ClientInfoRequestModel
import com.meter.giga.data.models.requests.ConnectionInfoRequestModel
import com.meter.giga.data.models.requests.LastClientMeasurementRequestModel
import com.meter.giga.data.models.requests.LastServerMeasurementRequestModel
import com.meter.giga.data.models.requests.ResultsRequestModel
import com.meter.giga.data.models.requests.ServerInfoRequestModel
import com.meter.giga.data.models.requests.SpeedTestMeasurementRequestModel
import com.meter.giga.data.models.requests.SpeedTestResultRequestModel
import com.meter.giga.data.models.requests.TCPInfoRequestModel
import com.meter.giga.data.models.responses.ClientInfoFallbackResponseModel
import com.meter.giga.data.models.responses.ClientInfoResponseModel
import com.meter.giga.data.models.responses.ServerInfoResponseModel
import com.meter.giga.domain.entity.request.BBRInfoRequestEntity
import com.meter.giga.domain.entity.request.ClientInfoRequestEntity
import com.meter.giga.domain.entity.request.ConnectionInfoRequestEntity
import com.meter.giga.domain.entity.request.LastClientMeasurementRequestEntity
import com.meter.giga.domain.entity.request.LastServerMeasurementRequestEntity
import com.meter.giga.domain.entity.request.ResultsRequestEntity
import com.meter.giga.domain.entity.request.ServerInfoRequestEntity
import com.meter.giga.domain.entity.request.SpeedTestMeasurementRequestEntity
import com.meter.giga.domain.entity.request.SpeedTestResultRequestEntity
import com.meter.giga.domain.entity.request.TCPInfoRequestEntity
import com.meter.giga.domain.entity.response.ClientInfoResponseEntity
import com.meter.giga.domain.entity.response.ServerInfoResponseEntity
import net.measurementlab.ndt7.android.models.BBRInfo
import net.measurementlab.ndt7.android.models.ConnectionInfo
import net.measurementlab.ndt7.android.models.Measurement
import net.measurementlab.ndt7.android.models.TCPInfo

fun ClientInfoResponseModel.toEntity(): ClientInfoResponseEntity? {
  return ClientInfoResponseEntity(
    asn = asn?.asn ?: "",
    city = city ?: "",
    isp = company?.name ?: "",
    country = country ?: "",
    ip = ip ?: "0",
    loc = loc ?: "",
    org = org ?: "",
    postal = postal ?: "",
    region = region ?: "",
    timezone = timezone ?: ""
  )
}

fun ServerInfoResponseModel.toEntity(): ServerInfoResponseEntity? {
  return ServerInfoResponseEntity(
    city = city ?: "",
    country = country ?: "",
    fqdn = fqdn ?: "",
    ipv4 = if (ip?.isNotEmpty() == true) {
      ip[0]
    } else "0",
    ipv6 = if (ip?.isNotEmpty() == true && ip.size > 1) {
      ip[1]
    } else "0",
    site = site ?: "",
    url = url ?: "",
    label = city ?: "",
    metro = site?.substring(0, 3) ?: ""
  )
}

fun ClientInfoFallbackResponseModel.toEntity(): ClientInfoResponseEntity? {
  return ClientInfoResponseEntity(
    asn = if (asn != null) "AS$asn" else "",
    city = city ?: "",
    isp = organization ?: organization_name ?: "",
    country = country ?: "",
    ip = ip ?: "",
    loc = "$latitude,$longitude",
    org = organization ?: organization_name ?: "",
    postal = "",
    region = region ?: "",
    timezone = timezone ?: ""
  )
}

fun SpeedTestResultRequestEntity.toModel(): SpeedTestResultRequestModel {
  return SpeedTestResultRequestModel(
    annotation = annotation,
    appVersion = appVersion,
    browserID = browserID,
    clientInfo = clientInfo?.toModel(),
    countryCode = countryCode,
    createdAt = createdAt,
    dataDownloaded = dataDownloaded,
    dataUploaded = dataUploaded,
    dataUsage = null,
    deviceType = deviceType,
    download = 0,
    gigaIdSchool = gigaIdSchool,
    id = id,
    ipAddress = ipAddress,
    latency = latency,
    notes = notes,
    results = results?.toModel(),
    schoolId = schoolId,
    serverInfo = serverInfo?.toModel(),
    source = source,
    timestamp = timestamp,
    uUID = uUID,
    upload = 0
  )
}

fun ClientInfoRequestEntity.toModel(): ClientInfoRequestModel {
  return ClientInfoRequestModel(
    aSN = asn ?: "",
    city = city,
    country = country,
    hostname = hostname,
    iP = ip,
    iSP = isp,
    latitude = latitude,
    longitude = longitude,
    postal = postal,
    region = region,
    timezone = timezone
  )
}

fun ResultsRequestEntity.toModel(): ResultsRequestModel {
  return ResultsRequestModel(
    ndtResultS2C = ndtResultS2C?.toModel(),
    ndtResultC2S = ndtResultC2S?.toModel()
  )
}

fun SpeedTestMeasurementRequestEntity.toModel(): SpeedTestMeasurementRequestModel {
  return SpeedTestMeasurementRequestModel(
    lastClientMeasurement = lastClientMeasurement?.toModel(),
    lastServerMeasurement = lastServerMeasurement?.toModel()
  )
}

fun LastClientMeasurementRequestEntity.toModel(): LastClientMeasurementRequestModel {
  return LastClientMeasurementRequestModel(
    elapsedTime = elapsedTime,
    meanClientMbps = meanClientMbps,
    numBytes = numBytes
  )
}

fun Measurement.toEntity(): LastServerMeasurementRequestEntity {
  return LastServerMeasurementRequestEntity(
    bbrInfo = bbrInfo?.toEntity(),
    connectionInfo = connectionInfo.toEntity(),
    tcpInfo = tcpInfo?.toEntity()
  )
}

fun BBRInfo.toEntity(): BBRInfoRequestEntity {
  return BBRInfoRequestEntity(
    bw = bw,
    cwndGain = cwndGain,
    elapsedTime = elapsedTime,
    minRTT = minRtt,
    pacingGain = pacingGain
  )
}

fun ConnectionInfo.toEntity(): ConnectionInfoRequestEntity {
  return ConnectionInfoRequestEntity(
    client = client,
    server = server,
    uuid = uuid
  )
}

fun TCPInfo.toEntity(): TCPInfoRequestEntity {
  return TCPInfoRequestEntity(
    ato = ato,
    advMSS = advMss,
    appLimited = appLimited,
    backoff = backoff,
    busyTime = busyTime,
    bytesAcked = bytesAcked,
    bytesReceived = bytesReceived,
    bytesRetrans = bytesRetrans,
    bytesSent = bytesSent,
    caState = caState,
    dSackDups = dSackDups,
    dataSegsIn = dataSegsIn,
    dataSegsOut = dataSegsOut,
    delivered = delivered,
    deliveredCE = deliveredCE,
    deliveryRate = deliveryRate,
    elapsedTime = elapsedTime,
    fackets = fackets,
    lastAckRecv = lastAckRecv,
    lastAckSent = lastAckSent,
    lastDataRecv = lastDataRecv,
    lastDataSent = lastDataSent,
    lost = lost,
    maxPacingRate = maxPacingRate,
    minRTT = minRtt,
    notsentBytes = notSentBytes,
    options = options,
    pmtu = pmtu,
    pacingRate = pacingRate,
    probes = probes,
    rto = rto,
    rtt = rtt,
    rttVar = rttVar,
    rWndLimited = rWndLimited,
    rcvMSS = rcvMss,
    rcvOooPack = 0,
    rcvRTT = rcvRtt,
    rcvSpace = rcvSpace,
    rcvSsThresh = rcvSsThresh,
    reordSeen = reordSeen,
    reordering = reordering,
    retrans = retrans,
    retransmits = retransmits,
    sacked = sacked,
    segsIn = segsIn,
    segsOut = segsOut,
    sndBufLimited = sndBufLimited,
    sndCwnd = sndCwnd,
    sndMSS = sndMss,
    sndSsThresh = sndSsThresh,
    sndWnd = sndCwnd,
    state = state,
    totalRetrans = totalRetrans,
    unacked = unacked,
    wScale = wScale
  )
}


fun LastServerMeasurementRequestEntity.toModel(): LastServerMeasurementRequestModel {
  return LastServerMeasurementRequestModel(
    bBRInfo = bbrInfo?.toModel(),
    connectionInfo = connectionInfo?.toModel(),
    tCPInfo = tcpInfo?.toModel()
  )
}

fun BBRInfoRequestEntity.toModel(): BBRInfoRequestModel {
  return BBRInfoRequestModel(
    bW = bw,
    cwndGain = cwndGain,
    elapsedTime = elapsedTime,
    minRTT = minRTT,
    pacingGain = pacingGain
  )
}

fun ConnectionInfoRequestEntity.toModel(): ConnectionInfoRequestModel {
  return ConnectionInfoRequestModel(
    client = client,
    server = server,
    uUID = uuid
  )
}

fun TCPInfoRequestEntity.toModel(): TCPInfoRequestModel {
  return TCPInfoRequestModel(
    aTO = ato,
    advMSS = advMSS,
    appLimited = appLimited,
    backoff = backoff,
    busyTime = busyTime,
    bytesAcked = bytesAcked,
    bytesReceived = bytesReceived,
    bytesRetrans = bytesRetrans,
    bytesSent = bytesSent,
    cAState = caState,
    dSackDups = dSackDups,
    dataSegsIn = dataSegsIn,
    dataSegsOut = dataSegsOut,
    delivered = delivered,
    deliveredCE = deliveredCE,
    deliveryRate = deliveryRate,
    elapsedTime = elapsedTime,
    fackets = fackets,
    lastAckRecv = lastAckRecv,
    lastAckSent = lastAckSent,
    lastDataRecv = lastDataRecv,
    lastDataSent = lastDataSent,
    lost = lost,
    maxPacingRate = maxPacingRate,
    minRTT = minRTT,
    notsentBytes = notsentBytes,
    options = options,
    pMTU = pmtu,
    pacingRate = pacingRate,
    probes = probes,
    rTO = rto,
    rTT = rtt,
    rTTVar = rttVar,
    rWndLimited = rWndLimited,
    rcvMSS = rcvMSS,
    rcvOooPack = rcvOooPack,
    rcvRTT = rcvRTT,
    rcvSpace = rcvSpace,
    rcvSsThresh = rcvSsThresh,
    reordSeen = reordSeen,
    reordering = reordering,
    retrans = retrans,
    retransmits = retransmits,
    sacked = sacked,
    segsIn = segsIn,
    segsOut = segsOut,
    sndBufLimited = sndBufLimited,
    sndCwnd = sndCwnd,
    sndMSS = sndMSS,
    sndSsThresh = sndSsThresh,
    sndWnd = sndWnd,
    state = state,
    totalRetrans = totalRetrans,
    unacked = unacked,
    wScale = wScale
  )
}


fun ServerInfoRequestEntity.toModel(): ServerInfoRequestModel {
  return ServerInfoRequestModel(
    city = city,
    country = country,
    fQDN = fQDN,
    iPv4 = iPv4,
    iPv6 = iPv6,
    label = label,
    metro = metro,
    site = site,
    uRL = uRL
  )
}
