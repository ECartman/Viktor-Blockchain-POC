/*
 *
 *  Copyright © ï¿½ 2019 Eduardo Vindas Cordoba. All rights reserved.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.aeongames.blockchain.base.MerkleTree;

import com.aeongames.blockchain.base.common.BinaryMath;
import com.aeongames.blockchain.base.common.Hash;
import com.aeongames.blockchain.base.transactions.ITransaction;
import static com.aeongames.logger.LoggingHelper.getDefaultLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Version 2.0 of Merkle tree implementation. this versions is a more Object
 * oriented version that the one provided by the Hyperledger code. i dunno the
 * reasoning behind the one on hyperledge but it is not easy to read nor a
 * efficient version. therefore i concluded to implement a Java version based on
 * the one from Bitcoin it is important to note that there is a latent
 * vulnerability that needs care when used this tree. for more information
 * please refer to: (CVE-2012-2459). but in short to avoid it BEFORE adding the
 * transactions to the merkle ensure the transaction has no duplicates
 *
 * i will base this implementation on 2 mayor versions on the one from the
 * hyperledger i expect to use few code from there but i now know how that one
 * works and although i don't like it there are a few thing that can be rescued,
 * and from the Bitcoin one. HOWEVER bitcoin implementation is more linear than
 * object oriented. (due speed most likely)
 * 
 * now this implementation can use more work. (specially on performance department) 
 * however from a academic perspective it does a good job. 
 *
 */
public final class MerkleTree2 {

    /**
     * this attribute will determine IF the tree is a full tree or partial (used
     * for transaction checking.
     *
     */
    private final boolean partialtree;

    /**
     * this attribute will contain the Root Hash of the Tree. this value Might
     * need to be recalculated to ensure consistency.
     */
    private MerkleNode RootHash;

    /**
     * the list of leafs contained on this tree.
     */
    private final ArrayList<MerkleNode> Leafs;
    /**
     * this object is not intended to be persistent.
     */
    private transient ArrayList<ArrayList<MerkleNode>> tree;

    public MerkleTree2(List<? extends ITransaction> Transactions) {//TODO: this needs to be a generic of type or that inherits a Transaction Object and not Object
        partialtree = false;
        Leafs = new ArrayList<>(Objects.requireNonNull(Transactions, "the Transaction List is Null").size());
        Transactions.forEach(Transaction -> Leafs.add(new MerkleNode(Transaction)));
    }

    private MerkleTree2(MerkleNode theConfirmationNode) {
        partialtree = true;
        Leafs = new ArrayList<>(2);
        MerkleNode clonedtree = theConfirmationNode.getTraceNodesOnly();
        RootHash = clonedtree.getRoot();
        Leafs.add(clonedtree.getParent().getsons()[0]);
        Leafs.add(clonedtree.getParent().getsons()[1]);
    }

    /**
     * gathers the root Hash if exist. if not will calculate the hash. this
     * might take some time as it is possible for the tree to contains the leaf
     * nodes only and will build the tree from there.
     *
     * @return a {@code}Hash that contains and represent the rootHash.
     */
    public Hash getRootHash() {
        if (RootHash == null && !partialtree) {
            CalculateRootHash();
        } else if (RootHash == null && partialtree) {
            throw new NullPointerException("this is a Partial Tree With no Root");
        }
        return RootHash.getID();
    }

    /**
     * in some cases we dont need more than just the nodes and the root. so we
     * will allow the tree removal. tho please have in mind that is cheaper to
     * have more memory than processing power to calculate the hashes.
     */
    public void crearTreedata() {
        tree.clear();
        tree = null;
    }

    public MerkleTree2 getComfirmationTreefor(ITransaction Transaction) {
        int indexofnode = Leafs.indexOf(new MerkleNode(Transaction));
        if (indexofnode >= 0) {
            return buildConfirmationTree(indexofnode);
        } else {
            return null;
        }
    }

    private MerkleTree2 buildConfirmationTree(int NodeIndex) {
        if (RootHash == null) {
            getRootHash();
        }
        return new MerkleTree2(Leafs.get(NodeIndex));
    }

    /**
     * calculates the root hash. it might also need to build the tree itself. as
     * the only assurance with this structure is that the Tree contains the Tree
     * leafs.
     */
    private void CalculateRootHash() {
        Objects.requireNonNull(Leafs, "the Transaction List is Null");
        if (!BinaryMath.fasteristwodivisible(Leafs.size())) {
            getDefaultLogger().log(Level.FINE, String.format("WARNING, The Tree Leafs are NOT a number divisible by two %d", Leafs.size()));
        }
        if (!BinaryMath.is_power_of_two(Leafs.size())) {
            getDefaultLogger().log(Level.FINE, String.format("WARNING, The Tree Leafs are NOT a two to a power %d", Leafs.size()));
        }
        //this shit will ensure the capacity, but will not track the size unless items are added. that inefficient
        //but we dont want to use array. as it is even more inefficient
        int expectedsize = BinaryMath.GetTreeDepthNeeded(Leafs.size()) + 1;//plus head.
        tree = new ArrayList<>(expectedsize);
        //first level will always have the "leafs"
        tree.add(Leafs);
        for (int index = 1; index < expectedsize; index++) {
            ArrayList<MerkleNode> source = tree.get(index - 1);
            //the size it will need to store the nodes
            int levelnodes = (int) Math.ceil(((double) source.size()) / 2d);
            ArrayList<MerkleNode> level = new ArrayList<>(levelnodes);
            for (int filler = 0; filler < source.size(); filler += 2) {
                //so we will get a error of the thing is not a perfect half. how to solve? 
                if (filler + 1 == source.size()) {
                    level.add(new MerkleNode(source.get(filler), source.get(filler)));
                    //WE ARE MUTATED! maybe we should notify someone bitcoin does althought is ignored for the most part... 
                } else {
                    level.add(new MerkleNode(source.get(filler), source.get(filler + 1)));
                }
            }
            tree.add(level);
        }
        RootHash = tree.get(tree.size() - 1).get(0);
    }

    /**
     * TODO: remove or edit. as this is currently for testing.
     */
    public void printTreebystructure() {
        if (RootHash != null) {
            for (int index = 0; index <= RootHash.getMerkleHeight(); index++) {
                System.out.print("Level " + index + " ");
                RootHash.getAllNodeFor(index).forEach(Node -> {
                    System.out.print(Node.getID().toString() + "  ||  ");
                });
                System.out.println();
            }
        }
    }

    /**
     * TODO: remove or edit. as this is currently for testing.
     */
    public void printTreebylevels() {
        if (tree != null) {
            tree.forEach(level -> {
                level.forEach(Node -> {
                    System.out.print(Node.getID().toString() + "  ||  ");
                });
                System.out.println();
            });
        }
    }

    /**
     * determine whenever or not this is a partial Tree (used for Confirmation
     * of a Merkle tree for a particular Transaction)
     *
     * @return true if this is a non full merkle tree false if this tree
     * contains all the nodes and information to build a Merkle Root NOTE a
     * Merkle Partial tree is likely to contain the root, but is warranted to be
     * incomplete.
     */
    public boolean isPartialTree() {
        return partialtree;
    }

    @Override
    public boolean equals(Object other) {
        if (Objects.nonNull(other)) {
            if (this == other) {
                return true;
            } else if (getClass() != other.getClass()) {
                return false;
            } else {
                if (RootHash != null) {
                    return RootHash.equals(((MerkleTree2) other).RootHash);
                    //this should not be needed it is unlikely the collition. 
                    //&&Leafs.equals(((MerkleTree2) other).Leafs);
                } else {
                    return Leafs.equals(((MerkleTree2) other).Leafs);
                }
            }
        }
        return false;
    }

    /**
     * please avoid for security reasons this is only used for JAVA usage please
     * read the hash instead!
     *
     * please avoid using this method is here so it can be used by java internal methods.
     * @return 32 bit JAVA! hash PLEASE AVOID
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.RootHash);
        hash = 97 * hash + Objects.hashCode(this.Leafs);
        return hash;
    }

    /**
     * internal class that represent each node of the tree. WARNING MerkleNode
     * is NEVER intended to be exposed outside this class if YOU THINK that
     * might be the case you are breaking the intention of this class.
     */
    private class MerkleNode
    {

        //private transient T representativeObject;//if the tree is serialized we don't want the Object reference to be stored
        /**
         * the current node Depth.
         */
        private int depth;
        /**
         * the hash fort the representativeObject
         */
        private Hash NodeHash;

        MerkleNode Parent = null, Sons[];

        MerkleNode(ITransaction transaction) {
            NodeHash = Hash.of(Objects.requireNonNull(transaction, "Transaction is null").getTransactionByteBuffer());
            depth = 0;
            Sons = null;
            Parent = null;//temporal
        }

        /**
         * creates a new leaf
         */
        private MerkleNode(Hash value, int depth) {
            NodeHash = value;
            this.depth = depth;
            Sons = null;
            Parent = null;//temporal
        }

        /**
         * creates a new leaf
         */
        MerkleNode(MerkleNode leafA, MerkleNode leafB) {
            depth = Math.max(leafA.getMerkleHeight(), leafB.getMerkleHeight()) + 1;
            NodeHash = Hash.merge(leafA.getID(), leafB.getID());
            connectNodes(leafA, leafB);
        }

        MerkleNode getParent() {
            return Parent;
        }

        private void setParent(MerkleNode node) {
            Parent = node;
        }

        Hash getID() {
            return NodeHash;
        }

        /**
         * The height in the tree. 0 is leaf.
         *
         * @return
         */
        int getMerkleHeight() {
            return depth;
        }

        /**
         * determines if this node is a Leaf or not.
         *
         * @return
         */
        boolean isLeaf() {
            return depth == 0;
        }

        @Override
        public boolean equals(Object other) {
            if (Objects.nonNull(other)) {
                if (this == other) {
                    return true;
                } else if (getClass() != other.getClass()) {
                    return false;
                } else {
                    return getID().equals(((MerkleNode) other).getID()) && depth == ((MerkleNode) other).depth;
                }
            }
            return false;
        }

        /**
         * please avoid for security reasons this is only used for JAVA usage
         * please read the hash instead!
         *
         * do not use please avoid
         * @return 32 bit JAVA! hash PLEASE AVOID
         */
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 83 * hash + this.depth;
            hash = 83 * hash + Objects.hashCode(this.NodeHash);
            return hash;
        }

        private void connectNodes(MerkleNode leafA, MerkleNode leafB) {
            leafA.setParent(this);
            leafB.setParent(this);
            Sons = new MerkleNode[2];
            Sons[0] = leafA;
            Sons[1] = leafB;
        }

        private MerkleNode[] getsons() {
            return Sons;
        }

        private MerkleNode getTraceNodesOnly() {
            if (Parent != null) {
                MerkleNode thisclone = getclone();
                MerkleNode other;
                if (Parent.getsons()[0] == this) {
                    other = Parent.getsons()[1].getclone();
                } else {
                    other = Parent.getsons()[0].getclone();
                }
                MerkleNode parentclone = Parent.getTraceNodesOnly();
                parentclone.connectNodes(thisclone, other);
                return thisclone;
            } else {
                return getclone();
            }
        }

        private MerkleNode getRoot() {
            if (Parent != null) {
                return Parent.getRoot();
            } else {
                return this;
            }
        }

        private MerkleNode getclone() {
            return new MerkleNode(this.NodeHash, this.depth);
        }

        private ArrayList<MerkleNode> getAllNodeFor(int index) {
            ArrayList<MerkleNode> nodes = new ArrayList<>();
            if (this.depth == index) {
                nodes.add(this);
            } else {
                if (Sons != null) {
                    for (MerkleNode Son : Sons) {
                        if (Son != null) {
                            nodes.addAll(Son.getAllNodeFor(index));
                        }
                    }
                }
            }
            return nodes;
        }
    }
}
