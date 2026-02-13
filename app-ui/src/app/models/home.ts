export interface Home {
  id: string;
  price: number;
  beds: number;
  baths: number;
  sqft: number;
  imageUrl: string;
  status: string;
  address: Address;
  description: string;
}

export interface Address {
  street: string;
  city: string;
  stateCode: string;
  zipCode: string;
  country: string;
}
