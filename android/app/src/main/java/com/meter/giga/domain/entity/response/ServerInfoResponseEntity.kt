package com.meter.giga.domain.entity.response

data class ServerInfoResponseEntity(
  val city: String?,
  val country: String?,
  val fqdn: String?,
  val ipv4: String?,
  val ipv6: String?,
  val site: String?,
  val url: String?,
  val label: String?,
  val metro: String?,
)


//"FQDN": "ndt-iupui-mlab2-bcn01.mlab-oti.measurement-lab.org",
//"IPv4": "0",
//"IPv6": "0",
//"City": "Barcelona",
//"Country": "ES",
//"Label": "Barcelona",
//"Metro": "bcn",
//"Site": "bcn01",
//"URL": "http://ndt-iupui-mlab2-bcn01.mlab-oti.measurement-lab.org:7123"
