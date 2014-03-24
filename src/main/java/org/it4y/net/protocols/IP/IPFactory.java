/*
 *
 *  * Copyright (C) 2014  Luc.Willems @ T.M.M.
 *  *
 *  * This file is released under the LGPL.
 *  * see license.txt for terms and conditions
 *
 */

package org.it4y.net.protocols.IP;

import org.it4y.net.protocols.IP.GRE.GREPacket;
import org.it4y.net.protocols.IP.ICMP.ICMPPacket;
import org.it4y.net.protocols.IP.IPIP.IPIPPacket;
import org.it4y.net.protocols.IP.IPv6Tunnel.IPv6TunnelPacket;
import org.it4y.net.protocols.IP.TCP.TCPPacket;
import org.it4y.net.protocols.IP.UDP.UDPPacket;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class IPFactory {

    private interface ipv4Factory {
        IpPacket create(ByteBuffer buffer, int size);
    }

    public static final Map<Byte, ipv4Factory> ipv4FactoryMap = Collections.unmodifiableMap(new HashMap<Byte, ipv4Factory>() {{
        put((byte) ICMPPacket.PROTOCOL, new ipv4Factory() {
            public IpPacket create(ByteBuffer buffer, int size) {
                return new ICMPPacket(buffer, size);
            }
        });
        put((byte) IPIPPacket.PROTOCOL, new ipv4Factory() {
            public IpPacket create(ByteBuffer buffer, int size) {
                return new IPIPPacket(buffer, size);
            }
        });
        put((byte) TCPPacket.PROTOCOL , new ipv4Factory() {
            public IpPacket create(ByteBuffer buffer, int size) {
                return new TCPPacket(buffer, size);
            }
        });
        put((byte) UDPPacket.PROTOCOL , new ipv4Factory() {
            public IpPacket create(ByteBuffer buffer, int size) {
                return new UDPPacket(buffer, size);
            }
        });
        put((byte) IPv6TunnelPacket.PROTOCOL , new ipv4Factory() {
            public IpPacket create(ByteBuffer buffer, int size) {
                return new IPv6TunnelPacket(buffer, size);
            }
        });
        put((byte) GREPacket.PROTOCOL , new ipv4Factory() {
            public IpPacket create(ByteBuffer buffer, int size) {
                return new GREPacket(buffer, size);
            }
        });
    }});

    public static final IpPacket processRawPacket(ByteBuffer buffer, int size) {
        //check for IPv4 and protocol
        if (buffer != null & size >0) {
            byte ipVersion = (byte) (buffer.get(IpPacket.header_version) >> 4);
            if (ipVersion == 4) {
                ipv4Factory factory = ipv4FactoryMap.get((byte) buffer.get(IpPacket.header_protocol));
                if (factory == null) {
                    return new IpPacket(buffer, size);
                } else {
                    return factory.create(buffer, size);
                }
             }
        }
        return null;
    }
}
