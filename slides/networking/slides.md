---
marp: true
theme: default
paginate: true
---

# Networking

## HSBC Graduate Training — Xi'an 2026

---

# The Human Packet Routing Game 🕹️

**You are the packet.** Your job: reach your destination address.

- **4 networks** (`10.1` / `10.2` / `10.3` / `10.4`) + a **CORE** router in the middle
- **Routers** hold a route table — show them your **destination**, they tell you the **next hop** (never the full path!)
- Cross off one **TTL** box per hop — TTL 0 means your packet **dies** 💀

| **UDP** 🏃 | **TCP** 🤝 |
|---|---|
| One person, fire & forget | Team of 3 segments (SEQ 1–3) |
| Dropped? Lost forever | Handshake first: SYN → SYN-ACK → ACK |
| Fast, no guarantees | Every segment gets an ACK — no ACK, walk again |

⚡ Watch out for **chaos cards**: link failures, firewalls, NAT, congestion...

**Winner:** most payloads delivered — dead packets count against you!
