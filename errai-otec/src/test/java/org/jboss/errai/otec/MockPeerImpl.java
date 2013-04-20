/*
 * Copyright 2013 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.otec;

import org.jboss.errai.otec.operation.OTOperation;
import org.jboss.errai.otec.operation.OTOperationImpl;
import org.jboss.errai.otec.util.OTLogFormat;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Brock
 */
public class MockPeerImpl implements OTPeer {
  private OTEngine localEngine;
  private OTEngine remoteEngine;

  private final Map<Integer, Integer> lastTransmittedSequencees = new HashMap<Integer, Integer>();

  public MockPeerImpl(final OTEngine localEngine, final OTEngine engine) {
    this.localEngine = localEngine;
    this.remoteEngine = engine;
  }

  @Override
  public String getId() {
    return remoteEngine.getId();
  }

  @Override
  public void send(final int entityId, final OTOperation operation) {
    OTLogFormat.log("TRANSMIT",
        operation.toString(),
        localEngine.toString(),
        remoteEngine.toString(),
        operation.getRevision(),
        "\"" + localEngine.getEntityStateSpace().getEntity(entityId).getState().get() + "\"");

    //note: this is simulating sending these operations over the wire.
    remoteEngine.getReceiveHandler(localEngine.getId(), entityId)
        .receive(OTOperationImpl.createLocalOnlyOperation(remoteEngine, operation));

    lastTransmittedSequencees.put(entityId, operation.getRevision());
  }

  @SuppressWarnings("unchecked")
  public void beginSyncRemoteEntity(final String peerId,
                                    final int entityId,
                                    final EntitySyncCompletionCallback<State> callback) {

    final OTEntity entity = remoteEngine.getEntityStateSpace().getEntity(entityId);
    localEngine.getEntityStateSpace().addEntity(new OTTestEntity(entity));

    OTLogFormat.log("SYNC",  "",
            remoteEngine.getEngineName(),
            localEngine.getEngineName(),
            entity.getRevision(),
            "\"" + entity.getState().get() + "\"");

    localEngine.associateEntity(remoteEngine.getId(), entityId);
    remoteEngine.associateEntity(localEngine.getId(), entityId);

    callback.syncComplete(entity);
  }


  @Override
  public int getLastKnownRemoteSequence(final OTEntity entity) {
    return 0;
  }

  @Override
  public int getLastTransmittedSequence(final OTEntity entity) {
    final Integer integer = lastTransmittedSequencees.get(entity.getId());
    return integer == null ? 0 : integer;
  }

  public String toString() {
    return remoteEngine.getEngineName();
  }

}
