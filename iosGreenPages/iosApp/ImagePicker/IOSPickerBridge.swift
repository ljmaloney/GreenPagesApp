import PhotosUI
import UIKit
import Foundation
import Shared

class IOSPickerBridge: NSObject, PHPickerViewControllerDelegate, IOSPickerDelegate {

    static let instance = IOSPickerBridge()

    static var callback: ((NSData, String) -> Void)?

    func pickImage(callback: @escaping (KotlinByteArray, String) -> Void) {
        IOSPickerBridge.pickImage { data, fileName in
            callback(data.toKotlinByteArray(), fileName)
        }
    }

    static func pickImage(callback: @escaping (NSData, String) -> Void) {
        self.callback = callback

        let picker = PHPickerViewController(configuration: PHPickerConfiguration())
        picker.delegate = IOSPickerBridge.instance

        UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }?
            .rootViewController?
            .present(picker, animated: true)
    }

    func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
        picker.dismiss(animated: true)

        guard let item = results.first else { return }

        item.itemProvider.loadDataRepresentation(forTypeIdentifier: "public.image") { data, _ in
            guard let data = data else { return }

            DispatchQueue.main.async {
                IOSPickerBridge.callback?(
                    data as NSData,
                    "img_ios.jpg"
                )
            }
        }
    }
}

extension NSData {
    func toKotlinByteArray() -> KotlinByteArray {
        let byteArray = KotlinByteArray(size: Int32(self.length))
        for i in 0..<self.length {
            byteArray.set(index: Int32(i), value: Int8(bitPattern: self.bytes.load(fromByteOffset: i, as: UInt8.self)))
        }
        return byteArray
    }
}


