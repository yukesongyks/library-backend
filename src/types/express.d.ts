// Augment Express Request to carry the resolved caller id.
declare module "express-serve-static-core" {
  interface Request {
    callerId?: string;
  }
}

export {};
