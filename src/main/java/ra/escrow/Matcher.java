package ra.escrow;

public interface Matcher {
    Boolean matches(Offer offer1, Offer offer2);
}
