# RA Escrow

## Entities
* Offer - An offer to buy or sell BTC with instructions
    * id - ID of offer
    * isBuy - true=Buy, false=Sell
    * amount - amount of currency offered to buy/sell BTC
    * currency - currency used to buy/sell BTC
    * instructions - the instructions to fulfill the offer
    * amountToBuySats - amount of Bitcoin to buy in Satoshis
    * minerFeeSats - Bitcoin blockchain fee estimated
    * txFeeSats -  Transaction Fee in Satoshis (Sats) estimated
    * fiatFloatSats - Float to take into consideration fiat volatility and blockchain fee estimation volatility
    * totalFundingSats - satsToBuy + minerFeeSats + txFeeSats + fiatFloatSats (estimate)
    * created - offer creation time
    * status - status of offer:
        * offered - user offers to buy or sell Bitcoin for instructions; can be canceled
        * matched - offer matched with another; can be canceled therefore must be verified prior to locking
        * locked - offer locked with Bitcoin in escrow and thus cannot be canceled
* Matcher - Determines if Offer matches any provided Offers in a List. There should be one instance of these for every Offer created locally in an Offered status. As new Offered Offers come in, the list need to be updated. As Locked Offers come in, removed.
* Matcher Task - Runs periodically Matchers.
* Escrow - The BTC address and amount to hold as automated 3rd party; each Escrow entry deleted upon completion
    * id - ID of escrow
    * buyOfferId - Buy Offer used to create this escrow
    * sellOfferId - Sell Offer used to create this escrow
    * escrowAddress - Bitcoin address used to hold funds
    * amount - Amount in satoshis to hold for this Escrow ()
    * timeSent - the start of the clock to determine when this escrow times out
    * buyerFee - total fees paid by buyer in Satoshis
    * receiverAddress - destination for Bitcoin
    * expiration - the time at which the escrow will expire
    * sellerFee - total fees paid by seller in Satoshis
    * status - status of escrow:
        * active - two offers matched and locked
        * expired - exchange not fulfilled in time
        * contested - exchange contested
        * fulfilled - exchange completed, escrow released
* Config - Configuration information required to manage the service
    * managerAddress - address by which manager can receive statistics
    * managerNetwork - network on which manager can be reached
    * managerPublicKey - public key to verify identity
    * managerSignatureKey - public key to verify message integrity
    * escrowAddress - address to use for escrow
* Stats - Statistics needed to manage the service
    * numberOffersUnlocked - all offered and matched offers
    * numberOffersLocked - all offers locked within escrows
    * numberEscrowsActive
    * escrowsExpired - full copy of each expired escrow; must resolve to no longer see in stats report
    * escrowsContested - full copy of each contested escrow; must resolve to no longer see in stats report

