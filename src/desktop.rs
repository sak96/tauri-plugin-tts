use serde::de::DeserializeOwned;
use tauri::{plugin::PluginApi, AppHandle, Runtime};

use crate::models::*;

pub fn init<R: Runtime, C: DeserializeOwned>(
    app: &AppHandle<R>,
    _api: PluginApi<R, C>,
) -> crate::Result<Tts<R>> {
    Ok(Tts(app.clone()))
}

/// Access to the tts APIs.
pub struct Tts<R: Runtime>(AppHandle<R>);

impl<R: Runtime> Tts<R> {
    pub fn speak(&self, text: SpeakArgs) -> crate::Result<()> {
        Err(crate::Error::UnsupportedPlatformError)
    }

    pub fn stop(&self) -> crate::Result<()> {
        Err(crate::Error::UnsupportedPlatformError)
    }

    pub fn set_voice(&self, _args: String) -> crate::Result<()> {
        Err(crate::Error::UnsupportedPlatformError)
    }

    pub fn get_all_voices(&self) -> crate::Result<GetVoicesResponse> {
        Err(crate::Error::UnsupportedPlatformError)
    }
}
