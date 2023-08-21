package com.ide.photoeditor.youshoot.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.ide.photoeditor.youshoot.R;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class DeleteDialog extends DialogFragment implements View.OnClickListener {
    OnClickListener m_clickListener;
    AlertDialog alertDialog;
    @BindView(R.id.mMbtnCancel)
    MaterialButton mMbtnCancel;
    @BindView(R.id.mMbtnDiscard)
    MaterialButton mMbtnDiscard;
    @BindView(R.id.mFrmNativeAdContainer)
    FrameLayout mFrmNativeAdContainer;

    public DeleteDialog(OnClickListener clickListener) {
        this.m_clickListener = clickListener;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.mMbtnDiscard) {
            m_clickListener.discard();
            alertDialog.dismiss();
        }
        if (view.getId() == R.id.mMbtnCancel) {
            alertDialog.dismiss();
        }
    }

    public interface OnClickListener {
        void discard();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = ((Activity) getActivity()).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_discard, null);
        dialogBuilder.setView(dialogView);

        if (alertDialog == null) {
            alertDialog = dialogBuilder.create();
        }
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.setCancelable(false);
        ButterKnife.bind(this, dialogView);

        mMbtnDiscard.setOnClickListener(this);

        mMbtnCancel.setOnClickListener(this);
      /*  try {

                BannerNativeAdUtils.loadSmallNativeAd(getContext(), mFrmNativeAdContainer);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return alertDialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        alertDialog = null;
    }
}
