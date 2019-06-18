import {
    Alert, ToastAndroid, Platform, Dimensions
} from 'react-native';

const Toast = {
showMessage : (message, cb = null) => {
        if (Platform.OS === 'ios') {
            return Alert.alert('', message,
                [
                    {
                        text: 'OK', onPress: () => {
                            var func = (typeof cb == 'function') ?
                                cb : eval(cb);

                        }
                    },
                ],
            )
        } else {
            return ToastAndroid.showWithGravity(message, ToastAndroid.SHORT, ToastAndroid.BOTTOM);
        }
    }
}

export default Toast;