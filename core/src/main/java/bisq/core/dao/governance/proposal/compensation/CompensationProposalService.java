/*
 * This file is part of Bisq.
 *
 * bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.core.dao.governance.proposal.compensation;

import bisq.core.btc.exceptions.TransactionVerificationException;
import bisq.core.btc.exceptions.WalletException;
import bisq.core.btc.wallet.BsqWalletService;
import bisq.core.btc.wallet.BtcWalletService;
import bisq.core.dao.exceptions.ValidationException;
import bisq.core.dao.governance.proposal.BaseProposalService;
import bisq.core.dao.governance.proposal.Proposal;
import bisq.core.dao.governance.proposal.ProposalConsensus;
import bisq.core.dao.governance.proposal.ProposalWithTransaction;
import bisq.core.dao.governance.proposal.TxException;
import bisq.core.dao.state.DaoStateService;
import bisq.core.dao.state.blockchain.OpReturnType;

import bisq.common.app.Version;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

/**
 * Creates the CompensationProposal and the transaction.
 */
@Slf4j
public class CompensationProposalService extends BaseProposalService<CompensationProposal> {

    private Coin requestedBsq;
    private String bsqAddress;

    @Inject
    public CompensationProposalService(BsqWalletService bsqWalletService,
                                       BtcWalletService btcWalletService,
                                       DaoStateService daoStateService,
                                       CompensationValidator proposalValidator) {
        super(bsqWalletService,
                btcWalletService,
                daoStateService,
                proposalValidator);
    }

    public ProposalWithTransaction createProposalWithTransaction(String name,
                                                                 String link,
                                                                 Coin requestedBsq,
                                                                 String bsqAddress)
            throws ValidationException, InsufficientMoneyException, TxException {
        this.requestedBsq = requestedBsq;
        this.bsqAddress = bsqAddress;

        return super.createProposalWithTransaction(name, link);
    }

    @Override
    protected CompensationProposal createProposalWithoutTxId() {
        return new CompensationProposal(
                name,
                link,
                requestedBsq,
                bsqAddress);
    }

    @Override
    protected byte[] getOpReturnData(byte[] hashOfPayload) {
        return ProposalConsensus.getOpReturnData(hashOfPayload,
                OpReturnType.COMPENSATION_REQUEST.getType(),
                Version.COMPENSATION_REQUEST);
    }

    @Override
    protected Transaction completeTx(Transaction preparedBurnFeeTx, byte[] opReturnData, Proposal proposal)
            throws WalletException, InsufficientMoneyException, TransactionVerificationException {
        CompensationProposal compensationProposal = (CompensationProposal) proposal;
        return btcWalletService.completePreparedCompensationRequestTx(
                compensationProposal.getRequestedBsq(),
                compensationProposal.getAddress(),
                preparedBurnFeeTx,
                opReturnData);
    }
}
