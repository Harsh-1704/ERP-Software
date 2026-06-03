export interface Party {
  id: number;
  partyName: string;
  legalName?: string;
  gstNumber?: string;
  panNumber?: string;
  status?: string;
  partyRoles?: PartyRole[];
  addresses?: Address[];
  contacts?: Contact[];
  createdAt?: string;
  updatedAt?: string;
}

export interface PartyRole {
  id: number;
  roleType: string;
  party?: Party;
}

export interface Address {
  id?: number;
  party?: { id: number };
  addressType?: string;
  street?: string;
  city?: string;
  state?: string;
  country?: string;
  pincode?: string;
  isDefault?: boolean;
}

export interface Contact {
  id?: number;
  party?: { id: number };
  contactName?: string;
  email?: string;
  phone?: string;
  mobile?: string;
  designation?: string;
  isPrimary?: boolean;
}
