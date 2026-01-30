export interface Home {
  id: string;        // UUID from Java translates to string in TS
  price: number;
  beds: number;
  baths: number;
  sqft: number;
  imageUrl: string;
  status: string;
  address: Address;  // Notice we use a nested interface for the address object
}

export interface Address {
  street: string;
  city: string;
  stateCode: string;
  zipCode: string;
  country: string;
}
