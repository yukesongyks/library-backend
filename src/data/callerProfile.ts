import type { CallerProfile } from "../types/caller.js";

const profiles: Record<string, CallerProfile> = {
  "demo-user": {
    caller_id: "demo-user",
    caller_type: "internal",
    caller_level: "L2",
    caller_dept: "platform",
  },
  alice: {
    caller_id: "alice",
    caller_type: "external",
    caller_level: "L1",
    caller_dept: "finance",
  },
  bob: {
    caller_id: "bob",
    caller_type: "internal",
    caller_level: "L3",
    caller_dept: "security",
  },
};

const unknownProfile = (callerId: string): CallerProfile => ({
  caller_id: callerId,
  caller_type: "unknown",
  caller_level: "unknown",
  caller_dept: "unknown",
});

export function getCallerProfile(callerId: string): CallerProfile {
  return profiles[callerId] ?? unknownProfile(callerId);
}
