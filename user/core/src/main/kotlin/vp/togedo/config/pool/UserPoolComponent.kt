package vp.togedo.config.pool

import org.apache.commons.pool2.BasePooledObjectFactory
import org.apache.commons.pool2.PooledObject
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.springframework.stereotype.Component
import vp.togedo.model.documents.user.Oauth
import vp.togedo.model.documents.user.UserDocument

@Component
class UserPoolComponent: BasePooledObjectFactory<UserDocument>() {

    override fun create(): UserDocument {
        return UserDocument(
            name = "",
            oauth = Oauth()
        )
    }

    override fun wrap(userDocument: UserDocument): PooledObject<UserDocument> {
        return DefaultPooledObject(userDocument)
    }
}