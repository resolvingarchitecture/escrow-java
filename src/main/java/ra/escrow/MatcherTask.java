package ra.escrow;

import ra.util.tasks.BaseTask;
import ra.util.tasks.TaskRunner;

import java.util.List;

/**
 * Iterates through Matchers.
 */
public class MatcherTask extends BaseTask {

    private Matcher matcher;
    private Offer offer;
    private Offer firstMatchedOffer = null;
    private List<Offer> offersToCheck;

    public MatcherTask(Offer offer, TaskRunner taskRunner) {
        super(MatcherTask.class.getSimpleName(), taskRunner);
    }

    public void setOffersToCheck(List<Offer> offersToCheck) {
        this.offersToCheck = offersToCheck;
    }

    public Offer getFirstMatchedOffer() {
        return firstMatchedOffer;
    }

    @Override
    public Boolean execute() {
        for(Offer o : offersToCheck) {
            if(matcher.matches(offer, o)) {
                firstMatchedOffer = o;
                status = Status.Completed;
                return true;
            }
        }
        return true;
    }
}
